package cn.jcodenest.framework.idempotent.core.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 幂等 Redis DAO
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AllArgsConstructor
public class IdempotentRedisDAO {

    /**
     * 幂等操作
     * <p>
     * KEY   格式：idempotent:%s // 参数为 uuid
     * VALUE 格式：String
     * 过期时间：不固定
     */
    private static final String IDEMPOTENT = "idempotent:%s";

    /**
     * Redis 模板
     */
    private final StringRedisTemplate redisTemplate;

    /**
     * 设置幂等 Key
     *
     * @param key     幂等的 Key
     * @param timeout 过期时间
     * @param timeUnit 时间单位
     * @return 是否成功
     */
    public Boolean setIfAbsent(String key, long timeout, TimeUnit timeUnit) {
        String redisKey = formatKey(key);
        return redisTemplate.opsForValue().setIfAbsent(redisKey, "", timeout, timeUnit);
    }

    /**
     * 删除幂等 Key
     *
     * @param key 幂等的 Key
     */
    public void delete(String key) {
        String redisKey = formatKey(key);
        redisTemplate.delete(redisKey);
    }

    /**
     * 格式化 Key
     *
     * @param key 幂等的 Key
     * @return 格式化后的 Key
     */
    private static String formatKey(String key) {
        return String.format(IDEMPOTENT, key);
    }
}
