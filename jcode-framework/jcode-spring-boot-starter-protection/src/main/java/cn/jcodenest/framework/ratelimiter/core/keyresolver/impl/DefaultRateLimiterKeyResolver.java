package cn.jcodenest.framework.ratelimiter.core.keyresolver.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.jcodenest.framework.ratelimiter.core.annotation.RateLimiter;
import cn.jcodenest.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import org.aspectj.lang.JoinPoint;

/**
 * 默认（全局级别）限流 Key 解析器，使用【方法名 + 方法参数】，组装成一个 Key
 * 为了避免 Key 过长，使用 MD5 进行 “压缩”
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class DefaultRateLimiterKeyResolver implements RateLimiterKeyResolver {

    /**
     * 解析一个 Key
     *
     * @param joinPoint   AOP 切面
     * @param rateLimiter 限流注解
     * @return Key
     */
    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        String methodName = joinPoint.getSignature().toString();
        String argsStr = StrUtil.join(",", joinPoint.getArgs());
        return SecureUtil.md5(methodName + argsStr);
    }
}
