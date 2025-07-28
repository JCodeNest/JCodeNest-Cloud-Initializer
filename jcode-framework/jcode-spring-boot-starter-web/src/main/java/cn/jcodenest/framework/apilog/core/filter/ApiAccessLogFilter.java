package cn.jcodenest.framework.apilog.core.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.apilog.core.annotation.ApiAccessLog;
import cn.jcodenest.framework.apilog.core.enums.OperateTypeEnum;
import cn.jcodenest.framework.common.biz.infra.logger.ApiAccessLogCommonApi;
import cn.jcodenest.framework.common.biz.infra.logger.dto.ApiAccessLogCreateReqDTO;
import cn.jcodenest.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.jcodenest.framework.common.pojo.CommonResult;
import cn.jcodenest.framework.common.util.json.JsonUtils;
import cn.jcodenest.framework.common.util.monitor.TracerUtils;
import cn.jcodenest.framework.common.util.servlet.ServletUtils;
import cn.jcodenest.framework.web.config.WebProperties;
import cn.jcodenest.framework.web.core.filter.ApiRequestFilter;
import cn.jcodenest.framework.web.core.util.WebFrameworkUtils;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Map;

import static cn.jcodenest.framework.apilog.core.interceptor.ApiAccessLogInterceptor.ATTRIBUTE_HANDLER_METHOD;
import static cn.jcodenest.framework.common.util.json.JsonUtils.toJsonString;

/**
 * API 访问日志过滤器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/28
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class ApiAccessLogFilter extends ApiRequestFilter {

    /**
     * 敏感参数
     */
    private static final String[] SANITIZE_KEYS = new String[]{"password", "token", "accessToken", "refreshToken"};

    /**
     * 应用名称
     */
    private final String applicationName;

    /**
     * API 访问日志通用 API
     */
    private final ApiAccessLogCommonApi apiAccessLogApi;

    /**
     * 构造方法
     *
     * @param webProperties   Web 配置
     * @param applicationName 应用名称
     * @param apiAccessLogApi API 访问日志通用 API
     */
    public ApiAccessLogFilter(WebProperties webProperties, String applicationName, ApiAccessLogCommonApi apiAccessLogApi) {
        super(webProperties);
        this.applicationName = applicationName;
        this.apiAccessLogApi = apiAccessLogApi;
    }

    /**
     * 执行过滤
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取开始时间
        LocalDateTime beginTime = LocalDateTime.now();

        // 提前获取参数, 避免 XssFilter 过滤处理
        Map<String, String> queryString = ServletUtils.getParamMap(request);

        // 提前获取 body, 避免 RequestBodyCacheFilter 过滤处理
        String requestBody = ServletUtils.isJsonRequest(request) ? ServletUtils.getBody(request) : null;

        try {
            // 继续过滤器
            filterChain.doFilter(request, response);

            // 正常执行, 记录日志
            createApiAccessLog(request, beginTime, queryString, requestBody, null);
        } catch (Exception ex) {
            // 异常执行, 记录日志
            createApiAccessLog(request, beginTime, queryString, requestBody, ex);
            throw ex;
        }
    }

    /**
     * 创建 API 访问日志
     *
     * @param request     请求
     * @param beginTime   开始时间
     * @param queryString 查询参数
     * @param requestBody 请求体
     * @param ex          异常
     */
    private void createApiAccessLog(HttpServletRequest request, LocalDateTime beginTime,
                                    Map<String, String> queryString, String requestBody, Exception ex) {
        ApiAccessLogCreateReqDTO accessLog = new ApiAccessLogCreateReqDTO();
        try {
            boolean enable = buildApiAccessLog(accessLog, request, beginTime, queryString, requestBody, ex);
            if (!enable) {
                return;
            }

            apiAccessLogApi.createApiAccessLogAsync(accessLog);
        } catch (Throwable throwable) {
            log.error("[createApiAccessLog][url({}) log({}) 发生异常]", request.getRequestURI(), toJsonString(accessLog), throwable);
        }
    }

    /**
     * 构建 API 访问日志
     *
     * @param accessLog   API 访问日志
     * @param request     请求
     * @param beginTime   开始时间
     * @param queryString 查询参数
     * @param requestBody 请求体
     * @param ex          异常
     * @return 是否开启
     */
    private boolean buildApiAccessLog(ApiAccessLogCreateReqDTO accessLog, HttpServletRequest request, LocalDateTime beginTime,
                                      Map<String, String> queryString, String requestBody, Exception ex) {
        // 判断: 是否要记录操作日志
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(ATTRIBUTE_HANDLER_METHOD);

        ApiAccessLog accessLogAnnotation = null;
        if (handlerMethod != null) {
            accessLogAnnotation = handlerMethod.getMethodAnnotation(ApiAccessLog.class);
            if (accessLogAnnotation != null && BooleanUtil.isFalse(accessLogAnnotation.enable())) {
                return false;
            }
        }

        // 处理用户信息
        accessLog.setUserId(WebFrameworkUtils.getLoginUserId(request));
        accessLog.setUserType(WebFrameworkUtils.getLoginUserType(request));

        // 设置访问结果
        CommonResult<?> result = WebFrameworkUtils.getCommonResult(request);
        if (result != null) {
            accessLog.setResultCode(result.getCode());
            accessLog.setResultMsg(result.getMsg());
        } else if (ex != null) {
            accessLog.setResultCode(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode());
            accessLog.setResultMsg(ExceptionUtil.getRootCauseMessage(ex));
        } else {
            accessLog.setResultCode(GlobalErrorCodeConstants.SUCCESS.getCode());
            accessLog.setResultMsg("");
        }

        // 设置请求字段
        accessLog.setTraceId(TracerUtils.getTraceId());
        accessLog.setApplicationName(applicationName);
        accessLog.setRequestUrl(request.getRequestURI());
        accessLog.setRequestMethod(request.getMethod());
        accessLog.setUserAgent(ServletUtils.getUserAgent(request));
        accessLog.setUserIp(ServletUtils.getClientIP(request));

        String[] sanitizeKeys = accessLogAnnotation != null ? accessLogAnnotation.sanitizeKeys() : null;
        Boolean requestEnable = accessLogAnnotation != null ? accessLogAnnotation.requestEnable() : Boolean.TRUE;

        // 默认记录, 所以判断 !false
        if (!BooleanUtil.isFalse(requestEnable)) {
            Map<String, Object> requestParams = MapUtil.<String, Object>builder()
                    .put("query", sanitizeMap(queryString, sanitizeKeys))
                    .put("body", sanitizeJson(requestBody, sanitizeKeys)).build();
            accessLog.setRequestParams(toJsonString(requestParams));
        }

        // 默认不记录, 默认强制要求 true
        Boolean responseEnable = accessLogAnnotation != null ? accessLogAnnotation.responseEnable() : Boolean.FALSE;
        if (BooleanUtil.isTrue(responseEnable)) {
            accessLog.setResponseBody(sanitizeJson(result, sanitizeKeys));
        }

        // 持续时间
        accessLog.setBeginTime(beginTime);
        accessLog.setEndTime(LocalDateTime.now());
        accessLog.setDuration((int) LocalDateTimeUtil.between(accessLog.getBeginTime(), accessLog.getEndTime(), ChronoUnit.MILLIS));

        // 操作模块
        if (handlerMethod != null) {
            Tag tagAnnotation = handlerMethod.getBeanType().getAnnotation(Tag.class);
            Operation operationAnnotation = handlerMethod.getMethodAnnotation(Operation.class);

            String operateModule;
            if (accessLogAnnotation != null && StrUtil.isNotBlank(accessLogAnnotation.operateModule())) {
                operateModule = accessLogAnnotation.operateModule();
            } else if (tagAnnotation != null) {
                operateModule = StrUtil.nullToDefault(tagAnnotation.name(), tagAnnotation.description());
            } else {
                operateModule = null;
            }

            String operateName;
            if (accessLogAnnotation != null && StrUtil.isNotBlank(accessLogAnnotation.operateName())) {
                operateName = accessLogAnnotation.operateName();
            } else if (operationAnnotation != null) {
                operateName = operationAnnotation.summary();
            } else {
                operateName = null;
            }

            OperateTypeEnum operateType = accessLogAnnotation != null && accessLogAnnotation.operateType().length > 0 ?
                    accessLogAnnotation.operateType()[0] : parseOperateLogType(request);

            accessLog.setOperateModule(operateModule);
            accessLog.setOperateName(operateName);
            accessLog.setOperateType(operateType.getType());
        }
        return true;
    }

    // ========== 解析 @ApiAccessLog、@Swagger 注解  ==========

    /**
     * 解析操作类型
     *
     * @param request 请求
     * @return 操作类型
     */
    private static OperateTypeEnum parseOperateLogType(HttpServletRequest request) {
        RequestMethod requestMethod = RequestMethod.resolve(request.getMethod());
        if (requestMethod == null) {
            return OperateTypeEnum.OTHER;
        }

        return switch (requestMethod) {
            case GET -> OperateTypeEnum.GET;
            case POST -> OperateTypeEnum.CREATE;
            case PUT -> OperateTypeEnum.UPDATE;
            case DELETE -> OperateTypeEnum.DELETE;
            default -> OperateTypeEnum.OTHER;
        };
    }

    // ========== 请求和响应的脱敏逻辑, 移除类似 password、token 等敏感字段 ==========

    /**
     * 脱敏 Map
     *
     * @param map          Map
     * @param sanitizeKeys 脱敏的 key
     * @return 脱敏后的 Map
     */
    private static String sanitizeMap(Map<String, ?> map, String[] sanitizeKeys) {
        if (CollUtil.isEmpty(map)) {
            return null;
        }

        if (sanitizeKeys != null) {
            MapUtil.removeAny(map, sanitizeKeys);
        }

        MapUtil.removeAny(map, SANITIZE_KEYS);
        return toJsonString(map);
    }

    /**
     * 脱敏 JSON
     *
     * @param jsonString   JSON 字符串
     * @param sanitizeKeys 脱敏的 key
     * @return 脱敏后的 JSON 字符串
     */
    private static String sanitizeJson(String jsonString, String[] sanitizeKeys) {
        if (StrUtil.isEmpty(jsonString)) {
            return null;
        }

        try {
            JsonNode rootNode = JsonUtils.parseTree(jsonString);
            sanitizeJson(rootNode, sanitizeKeys);
            return toJsonString(rootNode);
        } catch (Exception e) {
            // 脱敏失败的情况下, 直接忽略异常, 避免影响用户请求
            log.error("[sanitizeJson][脱敏({}) 发生异常]", jsonString, e);
            return jsonString;
        }
    }

    /**
     * 脱敏 JSON
     *
     * @param commonResult CommonResult
     * @param sanitizeKeys 脱敏的 key
     * @return 脱敏后的 JSON 字符串
     */
    private static String sanitizeJson(CommonResult<?> commonResult, String[] sanitizeKeys) {
        if (commonResult == null) {
            return null;
        }

        String jsonString = toJsonString(commonResult);

        try {
            // 只处理 data 字段, 不处理 code、msg 字段, 避免错误被脱敏掉
            JsonNode rootNode = JsonUtils.parseTree(jsonString);
            sanitizeJson(rootNode.get("data"), sanitizeKeys);
            return toJsonString(rootNode);
        } catch (Exception e) {
            // 脱敏失败的情况下, 直接忽略异常, 避免影响用户请求
            log.error("[sanitizeJson][脱敏({}) 发生异常]", jsonString, e);
            return jsonString;
        }
    }

    /**
     * 脱敏 JSON
     *
     * @param node         JSON 节点
     * @param sanitizeKeys 脱敏的 key
     */
    private static void sanitizeJson(JsonNode node, String[] sanitizeKeys) {
        // 情况一: 数组, 遍历处理
        if (node.isArray()) {
            for (JsonNode childNode : node) {
                sanitizeJson(childNode, sanitizeKeys);
            }
            return;
        }

        // 情况二: 非 Object, 只是某个值, 直接返回
        if (!node.isObject()) {
            return;
        }

        //  情况三: Object 遍历处理
        Iterator<Map.Entry<String, JsonNode>> iterator = node.properties().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            if (ArrayUtil.contains(sanitizeKeys, entry.getKey()) || ArrayUtil.contains(SANITIZE_KEYS, entry.getKey())) {
                iterator.remove();
                continue;
            }

            sanitizeJson(entry.getValue(), sanitizeKeys);
        }
    }
}
