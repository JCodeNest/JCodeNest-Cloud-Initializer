package cn.jcodenest.framework.signature.config;

import cn.jcodenest.framework.redis.config.JCodeRedisAutoConfiguration;
import cn.jcodenest.framework.signature.core.aop.ApiSignatureAspect;
import cn.jcodenest.framework.signature.core.redis.ApiSignatureRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * HTTP API 签名自动配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration(after = JCodeRedisAutoConfiguration.class)
public class JCodeApiSignatureAutoConfiguration {

    /**
     * 签名切面
     *
     * @param signatureRedisDAO 签名RedisDAO
     * @return 签名切面
     */
    @Bean
    public ApiSignatureAspect signatureAspect(ApiSignatureRedisDAO signatureRedisDAO) {
        return new ApiSignatureAspect(signatureRedisDAO);
    }

    /**
     * 签名RedisDAO
     *
     * @param stringRedisTemplate Redis模板
     * @return 签名RedisDAO
     */
    @Bean
    public ApiSignatureRedisDAO signatureRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new ApiSignatureRedisDAO(stringRedisTemplate);
    }
}
