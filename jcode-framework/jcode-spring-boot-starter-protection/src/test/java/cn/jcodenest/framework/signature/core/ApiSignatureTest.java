package cn.jcodenest.framework.signature.core;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.jcodenest.framework.signature.core.annotation.ApiSignature;
import cn.jcodenest.framework.signature.core.aop.ApiSignatureAspect;
import cn.jcodenest.framework.signature.core.redis.ApiSignatureRedisDAO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link ApiSignatureTest} 单元测试
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@ExtendWith(MockitoExtension.class)
class ApiSignatureTest {

    /**
     * HTTP API 签名切面
     */
    @InjectMocks
    private ApiSignatureAspect apiSignatureAspect;

    /**
     * HTTP API 签名 Redis DAO
     */
    @Mock
    private ApiSignatureRedisDAO signatureRedisDAO;

    @Test
    void testSignatureGet() throws IOException {
        // 创建签名
        Long timestamp = System.currentTimeMillis();
        String nonce = IdUtil.randomUUID();
        String appId = "xxxxxx";
        String appSecret = "yyyyyy";
        String signString = "k1=v1&v1=k1testappId=xxxxxx&nonce=" + nonce + "&timestamp=" + timestamp + "yyyyyy";
        String sign = DigestUtil.sha256Hex(signString);

        // 准备参数
        ApiSignature apiSignature = mock(ApiSignature.class);
        when(apiSignature.appId()).thenReturn("appId");
        when(apiSignature.timestamp()).thenReturn("timestamp");
        when(apiSignature.nonce()).thenReturn("nonce");
        when(apiSignature.sign()).thenReturn("sign");
        when(apiSignature.timeout()).thenReturn(60);
        when(apiSignature.timeUnit()).thenReturn(TimeUnit.SECONDS);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(eq("appId"))).thenReturn(appId);
        when(request.getHeader(eq("timestamp"))).thenReturn(String.valueOf(timestamp));
        when(request.getHeader(eq("nonce"))).thenReturn(nonce);
        when(request.getHeader(eq("sign"))).thenReturn(sign);
        when(request.getParameterMap()).thenReturn(MapUtil.<String, String[]>builder().put("v1", new String[]{"k1"}).put("k1", new String[]{"v1"}).build());
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("test")));

        // mock 方法
        when(signatureRedisDAO.getAppSecret(eq(appId))).thenReturn(appSecret);
        when(signatureRedisDAO.setNonce(eq(appId), eq(nonce), eq(120), eq(TimeUnit.SECONDS))).thenReturn(true);

        // 调用
        boolean result = apiSignatureAspect.verifySignature(apiSignature, request);

        // 断言结果
        assertTrue(result);
    }
}
