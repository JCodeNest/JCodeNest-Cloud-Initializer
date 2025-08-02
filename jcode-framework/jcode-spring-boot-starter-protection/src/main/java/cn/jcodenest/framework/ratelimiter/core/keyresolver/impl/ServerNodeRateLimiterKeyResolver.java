package cn.jcodenest.framework.ratelimiter.core.keyresolver.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.system.SystemUtil;
import cn.jcodenest.framework.ratelimiter.core.annotation.RateLimiter;
import cn.jcodenest.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import org.aspectj.lang.JoinPoint;

/**
 * Server 节点级别的限流 Key 解析器，使用【方法名 + 方法参数 + IP】，组装成一个 Key
 * 为了避免 Key 过长，使用 MD5 进行 “压缩”
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class ServerNodeRateLimiterKeyResolver implements RateLimiterKeyResolver {

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
        String serverNode = String.format("%s@%d", SystemUtil.getHostInfo().getAddress(), SystemUtil.getCurrentPID());
        return SecureUtil.md5(methodName + argsStr + serverNode);
    }
}
