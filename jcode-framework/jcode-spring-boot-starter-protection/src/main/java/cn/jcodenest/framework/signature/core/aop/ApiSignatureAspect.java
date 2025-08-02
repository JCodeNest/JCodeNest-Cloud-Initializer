package cn.jcodenest.framework.signature.core.aop;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.jcodenest.framework.common.exception.ServiceException;
import cn.jcodenest.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.jcodenest.framework.common.util.servlet.ServletUtils;
import cn.jcodenest.framework.signature.core.annotation.ApiSignature;
import cn.jcodenest.framework.signature.core.redis.ApiSignatureRedisDAO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import static cn.jcodenest.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 拦截声明了 {@link ApiSignature} 注解的方法实现签名
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@Aspect
@AllArgsConstructor
public class ApiSignatureAspect {

    /**
     * HTTP API 签名 Redis DAO
     */
    private final ApiSignatureRedisDAO signatureRedisDAO;

    /**
     * 验证签名
     *
     * @param joinPoint 切点
     * @param signature 签名注解
     */
    @Before("@annotation(signature)")
    public void beforePointCut(JoinPoint joinPoint, ApiSignature signature) {
        // 验证通过，直接结束
        if (verifySignature(signature, Objects.requireNonNull(ServletUtils.getRequest()))) {
            return;
        }

        // 验证不通过，抛出异常
        log.error("[beforePointCut][方法{} 参数({}) 签名失败]", joinPoint.getSignature().toString(), joinPoint.getArgs());
        throw new ServiceException(BAD_REQUEST.getCode(), StrUtil.blankToDefault(signature.message(), BAD_REQUEST.getMsg()));
    }

    /**
     * 验证签名
     *
     * @param signature signature 注解
     * @param request   request 请求
     * @return 是否通过
     */
    public boolean verifySignature(ApiSignature signature, HttpServletRequest request) {
        // 校验 Header
        if (!verifyHeaders(signature, request)) {
            return false;
        }

        // 校验 appId 是否能获取到对应的 appSecret
        String appId = request.getHeader(signature.appId());
        String appSecret = signatureRedisDAO.getAppSecret(appId);
        Assert.notNull(appSecret, "[appId({})] 找不到对应的 appSecret", appId);

        // 校验签名【重要】
        // 客户端签名
        String clientSignature = request.getHeader(signature.sign());
        // 服务端签名字符串
        String serverSignatureString = buildSignatureString(signature, request, appSecret);
        // 服务端签名
        String serverSignature = DigestUtil.sha256Hex(serverSignatureString);
        if (ObjUtil.notEqual(clientSignature, serverSignature)) {
            return false;
        }

        // 将 nonce 记入缓存，防止重复使用（重点：此处需要将 ttl 设定为允许 timestamp 时间差的值 x 2 ）
        String nonce = request.getHeader(signature.nonce());
        if (BooleanUtil.isFalse(signatureRedisDAO.setNonce(appId, nonce, signature.timeout() * 2, signature.timeUnit()))) {
            String timestamp = request.getHeader(signature.timestamp());
            log.info("[verifySignature][appId({}) timestamp({}) nonce({}) sign({}) 存在重复请求]", appId, timestamp, nonce, clientSignature);
            throw new ServiceException(GlobalErrorCodeConstants.REPEATED_REQUESTS.getCode(), "存在重复请求");
        }

        return true;
    }

    /**
     * 校验请求头加签参数
     * <p>
     * 1. appId 是否为空
     * 2. timestamp 是否为空，请求是否已经超时，默认 10 分钟
     * 3. nonce 是否为空，随机数是否 10 位以上，是否在规定时间内已经访问过了
     * 4. sign 是否为空
     *
     * @param signature signature
     * @param request   request
     * @return 是否校验 Header 通过
     */
    private boolean verifyHeaders(ApiSignature signature, HttpServletRequest request) {
        // 非空校验
        String appId = request.getHeader(signature.appId());
        if (StrUtil.isBlank(appId)) {
            return false;
        }
        String timestamp = request.getHeader(signature.timestamp());
        if (StrUtil.isBlank(timestamp)) {
            return false;
        }
        String nonce = request.getHeader(signature.nonce());
        if (StrUtil.length(nonce) < 10) {
            return false;
        }
        String sign = request.getHeader(signature.sign());
        if (StrUtil.isBlank(sign)) {
            return false;
        }

        // 检查 timestamp 是否超出允许的范围 （重点：此处需要取绝对值）
        long expireTime = signature.timeUnit().toMillis(signature.timeout());
        long requestTimestamp = Long.parseLong(timestamp);
        long timestampDisparity = Math.abs(System.currentTimeMillis() - requestTimestamp);
        if (timestampDisparity > expireTime) {
            return false;
        }

        // 检查 nonce 是否存在，有且仅能使用一次
        return signatureRedisDAO.getNonce(appId, nonce) == null;
    }

    /**
     * 构建签名字符串
     * <p>
     * 格式为 = 请求参数 + 请求体 + 请求头 + 密钥
     *
     * @param signature signature
     * @param request   request
     * @param appSecret appSecret
     * @return 签名字符串
     */
    private String buildSignatureString(ApiSignature signature, HttpServletRequest request, String appSecret) {
        // 请求头
        SortedMap<String, String> headerMap = getRequestHeaderMap(signature, request);
        // 请求参数
        SortedMap<String, String> parameterMap = getRequestParameterMap(request);
        // 请求体
        String requestBody = StrUtil.nullToDefault(ServletUtils.getBody(request), "");
        return MapUtil.join(parameterMap, "&", "=")
                + requestBody
                + MapUtil.join(headerMap, "&", "=")
                + appSecret;
    }

    /**
     * 获取请求头加签参数 Map
     *
     * @param request   请求
     * @param signature 签名注解
     * @return signature params
     */
    private static SortedMap<String, String> getRequestHeaderMap(ApiSignature signature, HttpServletRequest request) {
        SortedMap<String, String> sortedMap = new TreeMap<>();
        sortedMap.put(signature.appId(), request.getHeader(signature.appId()));
        sortedMap.put(signature.timestamp(), request.getHeader(signature.timestamp()));
        sortedMap.put(signature.nonce(), request.getHeader(signature.nonce()));
        return sortedMap;
    }

    /**
     * 获取请求参数 Map
     *
     * @param request 请求
     * @return queryParams
     */
    private static SortedMap<String, String> getRequestParameterMap(HttpServletRequest request) {
        SortedMap<String, String> sortedMap = new TreeMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            sortedMap.put(entry.getKey(), entry.getValue()[0]);
        }
        return sortedMap;
    }
}
