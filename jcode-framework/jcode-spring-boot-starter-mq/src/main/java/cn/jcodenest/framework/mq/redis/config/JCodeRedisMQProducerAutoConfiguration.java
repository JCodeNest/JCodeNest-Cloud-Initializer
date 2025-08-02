package cn.jcodenest.framework.mq.redis.config;

import cn.jcodenest.framework.mq.redis.core.RedisMQTemplate;
import cn.jcodenest.framework.mq.redis.core.interceptor.RedisMessageInterceptor;
import cn.jcodenest.framework.redis.config.JCodeRedisAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * Redis 消息队列 Producer 配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@AutoConfiguration(after = JCodeRedisAutoConfiguration.class)
public class JCodeRedisMQProducerAutoConfiguration {

    /**
     * 创建 RedisMQTemplate
     *
     * @param redisTemplate Redis 模板
     * @param interceptors  消息拦截器
     * @return RedisMQTemplate
     */
    @Bean
    public RedisMQTemplate redisMQTemplate(StringRedisTemplate redisTemplate, List<RedisMessageInterceptor> interceptors) {
        // 添加拦截器
        RedisMQTemplate redisMQTemplate = new RedisMQTemplate(redisTemplate);
        interceptors.forEach(redisMQTemplate::addInterceptor);
        return redisMQTemplate;
    }
}
