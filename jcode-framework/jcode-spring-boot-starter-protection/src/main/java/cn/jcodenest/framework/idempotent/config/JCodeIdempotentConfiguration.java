package cn.jcodenest.framework.idempotent.config;

import cn.jcodenest.framework.idempotent.core.aop.IdempotentAspect;
import cn.jcodenest.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import cn.jcodenest.framework.idempotent.core.keyresolver.impl.DefaultIdempotentKeyResolver;
import cn.jcodenest.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import cn.jcodenest.framework.idempotent.core.keyresolver.impl.UserIdempotentKeyResolver;
import cn.jcodenest.framework.idempotent.core.redis.IdempotentRedisDAO;
import cn.jcodenest.framework.redis.config.JCodeRedisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * 幂等配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration(after = JCodeRedisAutoConfiguration.class)
public class JCodeIdempotentConfiguration {

    @Bean
    public IdempotentAspect idempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        return new IdempotentAspect(keyResolvers, idempotentRedisDAO);
    }

    @Bean
    public IdempotentRedisDAO idempotentRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentRedisDAO(stringRedisTemplate);
    }

    // ========== 各种 IdempotentKeyResolver Bean ==========

    @Bean
    public DefaultIdempotentKeyResolver defaultIdempotentKeyResolver() {
        return new DefaultIdempotentKeyResolver();
    }

    @Bean
    public UserIdempotentKeyResolver userIdempotentKeyResolver() {
        return new UserIdempotentKeyResolver();
    }

    @Bean
    public ExpressionIdempotentKeyResolver expressionIdempotentKeyResolver() {
        return new ExpressionIdempotentKeyResolver();
    }
}
