package cn.jcodenest.framework.ratelimiter.core.annotation;

import cn.jcodenest.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.jcodenest.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import cn.jcodenest.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import cn.jcodenest.framework.ratelimiter.core.keyresolver.impl.ClientIpRateLimiterKeyResolver;
import cn.jcodenest.framework.ratelimiter.core.keyresolver.impl.DefaultRateLimiterKeyResolver;
import cn.jcodenest.framework.ratelimiter.core.keyresolver.impl.ServerNodeRateLimiterKeyResolver;
import cn.jcodenest.framework.ratelimiter.core.keyresolver.impl.UserRateLimiterKeyResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    /**
     * 限流的时间，默认为 1 秒
     */
    int time() default 1;

    /**
     * 时间单位，默认为 SECONDS 秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限流次数，默认为 100 次
     */
    int count() default 100;

    /**
     * 提示信息，请求过快的提示
     * 为空时，使用 TOO_MANY_REQUESTS 错误提示
     *
     * @see GlobalErrorCodeConstants#TOO_MANY_REQUESTS
     */
    String message() default "";

    /**
     * 使用的 Key 解析器
     *
     * @see DefaultRateLimiterKeyResolver    全局级别
     * @see UserRateLimiterKeyResolver       用户 ID 级别
     * @see ClientIpRateLimiterKeyResolver   用户 IP 级别
     * @see ServerNodeRateLimiterKeyResolver 服务器 Node 级别
     * @see ExpressionIdempotentKeyResolver  自定义表达式，通过 {@link #keyArg()} 计算
     */
    Class<? extends RateLimiterKeyResolver> keyResolver() default DefaultRateLimiterKeyResolver.class;

    /**
     * 使用的 Key 参数
     */
    String keyArg() default "";
}
