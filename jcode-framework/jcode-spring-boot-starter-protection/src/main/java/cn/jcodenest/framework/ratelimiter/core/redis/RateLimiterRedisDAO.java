package cn.jcodenest.framework.ratelimiter.core.redis;

import lombok.AllArgsConstructor;
import org.redisson.api.*;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 限流 Redis DAO
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AllArgsConstructor
public class RateLimiterRedisDAO {

    /**
     * 限流操作
     * <p>
     * KEY   格式：rate_limiter:%s  // 参数为 uuid
     * VALUE 格式：String
     * 过期时间：不固定
     */
    private static final String RATE_LIMITER = "rate_limiter:%s";

    /**
     * Redisson 客户端
     */
    private final RedissonClient redissonClient;

    /**
     * 限流操作
     *
     * @param key    限流的 Key
     * @param count  限流次数
     * @param time   限流时间
     * @param timeUnit 限流时间单位
     * @return 是否成功
     */
    public Boolean tryAcquire(String key, int count, int time, TimeUnit timeUnit) {
        // 获取 RRateLimiter 并设置 rate 速率
        RRateLimiter rateLimiter = getRRateLimiter(key, count, time, timeUnit);
        // 尝试获取 1 个
        return rateLimiter.tryAcquire();
    }

    /**
     * 格式化 Key
     *
     * @param key 限流的 Key
     * @return 格式化后的 Key
     */
    private static String formatKey(String key) {
        return String.format(RATE_LIMITER, key);
    }

    /**
     * 获取 RRateLimiter
     *
     * @param key    限流的 Key
     * @param count  限流次数
     * @param time   限流时间
     * @param timeUnit 限流时间单位
     * @return RRateLimiter
     */
    private RRateLimiter getRRateLimiter(String key, long count, int time, TimeUnit timeUnit) {
        String redisKey = formatKey(key);
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(redisKey);
        Duration rateInterval = Duration.ofSeconds(timeUnit.toSeconds(time));

        // 如果不存在，设置 rate 速率
        RateLimiterConfig config = rateLimiter.getConfig();
        if (config == null) {
            rateLimiter.trySetRate(RateType.OVERALL, count, rateInterval, rateInterval);
            return rateLimiter;
        }

        // 如果存在，并且配置相同，则直接返回
        if (config.getRateType() == RateType.OVERALL
                && Objects.equals(config.getRate(), count)
                && Objects.equals(Duration.ofMillis(config.getRateInterval()), rateInterval)) {
            return rateLimiter;
        }

        // 如果存在，并且配置不同，则进行新建
        rateLimiter.setRate(RateType.OVERALL, count, rateInterval, rateInterval);
        return rateLimiter;
    }
}
