package cn.jcodenest.framework.redis.config;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Objects;

/**
 * 基于 Redis 的自动配置类，优先于 RedissonAutoConfigurationV2 执行。
 *
 * <p>
 * 配置 RedisTemplate，使用 JSON 序列化方式支持 LocalDateTime 等 Java 8 时间类型的序列化。
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration(before = RedissonAutoConfigurationV2.class)
public class JCodeRedisAutoConfiguration {

    /**
     * 创建 RedisTemplate Bean，使用 JSON 序列化方式。
     *
     * @param factory Redis 连接工厂
     * @return RedisTemplate 实例
     * @throws NullPointerException 如果 factory 为 null
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        Objects.requireNonNull(factory, "RedisConnectionFactory must not be null");

        // 创建 RedisTemplate 对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置 RedisConnection 工厂
        template.setConnectionFactory(factory);
        // 使用 String 序列化方式，序列化 KEY 和 HASH KEY
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // 使用 JSON 序列化方式（基于 Jackson）序列化 VALUE 和 HASH VALUE
        RedisSerializer<?> serializer = buildRedisSerializer();
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        return template;
    }

    /**
     * 构建 JSON 序列化器，支持 LocalDateTime 等 Java 8 时间类型的序列化。
     *
     * @return RedisSerializer 实例
     * @throws IllegalStateException 如果无法创建 JSON 序列化器
     */
    @SuppressWarnings("unchecked")
    public static RedisSerializer<?> buildRedisSerializer() {
        // 创建 Jackson JSON 序列化器
        RedisSerializer<Object> json = RedisSerializer.json();
        Objects.requireNonNull(json, "JSON serializer must not be null");

        // 配置 ObjectMapper 支持 Java 8 时间类型
        ObjectMapper objectMapper = (ObjectMapper) ReflectUtil.getFieldValue(json, "mapper");
        objectMapper.registerModules(new JavaTimeModule());
        return json;
    }
}
