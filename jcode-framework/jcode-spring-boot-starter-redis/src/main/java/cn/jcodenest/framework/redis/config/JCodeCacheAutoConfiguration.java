package cn.jcodenest.framework.redis.config;

import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.redis.config.properties.JCodeCacheProperties;
import cn.jcodenest.framework.redis.core.TimeoutRedisCacheManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 基于 Redis 的缓存自动配置类。
 *
 * <p>
 * 配置 Redis 缓存，包括自定义过期时间的 RedisCacheManager 和 RedisCacheConfiguration。
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@EnableCaching
@AutoConfiguration
@EnableConfigurationProperties({CacheProperties.class, JCodeCacheProperties.class})
public class JCodeCacheAutoConfiguration {

    /**
     * 配置 RedisCacheConfiguration。
     * <p>
     * 1. 设置缓存键前缀为单冒号（:），避免 Redis Desktop Manager 显示多余空格。
     * 2. 使用 JSON 序列化方式存储缓存值。
     * 3. 应用 CacheProperties.Redis 的配置（如 TTL、是否缓存空值、是否使用键前缀）。
     * 参考：https://blog.csdn.net/chuixue24/article/details/103928965
     * Issues：https://gitee.com/zhijiantianya/yudao-cloud/issues/I86VY2
     * </p>
     *
     * @param cacheProperties Spring Boot 缓存属性配置
     * @return RedisCacheConfiguration 实例
     * @throws NullPointerException 如果 cacheProperties 为 null
     */
    @Bean
    @Primary
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        Objects.requireNonNull(cacheProperties, "CacheProperties must not be null");
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        // 设置缓存键前缀，使用单冒号（:）分隔
        config = config.computePrefixWith(cacheName -> {
            String keyPrefix = cacheProperties.getRedis().getKeyPrefix();
            if (StringUtils.hasText(keyPrefix)) {
                // 确保前缀以冒号结尾
                String prefix = keyPrefix.endsWith(StrUtil.COLON) ? keyPrefix : keyPrefix + StrUtil.COLON;
                return prefix + cacheName + StrUtil.COLON;
            }
            return cacheName + StrUtil.COLON;
        });

        // 设置 JSON 序列化方式
        config = config.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(JCodeRedisAutoConfiguration.buildRedisSerializer()));

        // 应用 CacheProperties.Redis 配置
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }

        return config;
    }

    /**
     * 配置 RedisCacheManager，支持自定义过期时间。
     *
     * @param redisTemplate           Redis 模板
     * @param redisCacheConfiguration Redis 缓存配置
     * @param jCodeCacheProperties    自定义缓存属性配置
     * @return RedisCacheManager 实例
     * @throws NullPointerException 如果 redisTemplate、redisCacheConfiguration 或 jCodeCacheProperties 为 null
     * @throws IllegalArgumentException 如果 redisScanBatchSize 小于或等于 0
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisTemplate<String, Object> redisTemplate,
                                               RedisCacheConfiguration redisCacheConfiguration,
                                               JCodeCacheProperties jCodeCacheProperties) {
        Objects.requireNonNull(redisTemplate, "RedisTemplate must not be null");
        Objects.requireNonNull(redisCacheConfiguration, "RedisCacheConfiguration must not be null");
        Objects.requireNonNull(jCodeCacheProperties, "JCodeCacheProperties must not be null");

        // 验证批量扫描大小
        int batchSize = jCodeCacheProperties.getRedisScanBatchSize();
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Redis scan batch size must be greater than 0");
        }

        // 创建 RedisCacheWriter
        RedisConnectionFactory connectionFactory = Objects.requireNonNull(redisTemplate.getConnectionFactory(),
                "RedisConnectionFactory must not be null");
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory,
                BatchStrategies.scan(batchSize));

        // 创建 TimeoutRedisCacheManager
        return new TimeoutRedisCacheManager(cacheWriter, redisCacheConfiguration);
    }
}
