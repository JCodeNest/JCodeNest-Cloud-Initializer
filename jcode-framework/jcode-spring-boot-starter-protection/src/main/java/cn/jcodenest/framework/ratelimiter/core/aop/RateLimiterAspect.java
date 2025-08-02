package cn.jcodenest.framework.ratelimiter.core.aop;

import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.common.exception.ServiceException;
import cn.jcodenest.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.jcodenest.framework.common.util.collection.CollectionUtils;
import cn.jcodenest.framework.ratelimiter.core.annotation.RateLimiter;
import cn.jcodenest.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import cn.jcodenest.framework.ratelimiter.core.redis.RateLimiterRedisDAO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * 拦截声明了 {@link RateLimiter} 注解的方法，实现限流操作
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
public class RateLimiterAspect {

    /**
     * RateLimiterKeyResolver 集合
     */
    private final Map<Class<? extends RateLimiterKeyResolver>, RateLimiterKeyResolver> keyResolvers;

    /**
     * 限流 Redis DAO
     */
    private final RateLimiterRedisDAO rateLimiterRedisDAO;

    /**
     * 构造函数
     *
     * @param keyResolvers 限流 Key 解析器列表
     * @param rateLimiterRedisDAO 限流 Redis DAO
     */
    public RateLimiterAspect(List<RateLimiterKeyResolver> keyResolvers, RateLimiterRedisDAO rateLimiterRedisDAO) {
        this.keyResolvers = CollectionUtils.convertMap(keyResolvers, RateLimiterKeyResolver::getClass);
        this.rateLimiterRedisDAO = rateLimiterRedisDAO;
    }

    /**
     * 切面方法
     *
     * @param joinPoint   AOP 切面
     * @param rateLimiter 限流注解
     */
    @Before("@annotation(rateLimiter)")
    public void beforePointCut(JoinPoint joinPoint, RateLimiter rateLimiter) {
        // 获取 IdempotentKeyResolver 对象
        RateLimiterKeyResolver keyResolver = keyResolvers.get(rateLimiter.keyResolver());
        Assert.notNull(keyResolver, "找不到对应的 RateLimiterKeyResolver");

        // 解析 Key
        String key = keyResolver.resolver(joinPoint, rateLimiter);

        // 获取 1 次限流
        boolean success = rateLimiterRedisDAO.tryAcquire(key, rateLimiter.count(), rateLimiter.time(), rateLimiter.timeUnit());
        if (!success) {
            log.info("[beforePointCut][方法({}) 参数({}) 请求过于频繁]", joinPoint.getSignature().toString(), joinPoint.getArgs());
            String message = StrUtil.blankToDefault(rateLimiter.message(), GlobalErrorCodeConstants.TOO_MANY_REQUESTS.getMsg());
            throw new ServiceException(GlobalErrorCodeConstants.TOO_MANY_REQUESTS.getCode(), message);
        }
    }
}
