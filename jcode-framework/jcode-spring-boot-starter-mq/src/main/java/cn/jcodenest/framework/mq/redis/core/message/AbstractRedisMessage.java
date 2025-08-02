package cn.jcodenest.framework.mq.redis.core.message;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis 消息抽象基类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
public abstract class AbstractRedisMessage {

    /**
     * 头
     */
    private Map<String, String> headers = new HashMap<>();

    /**
     * 获取头
     *
     * @param key 头键
     * @return 头值
     */
    public String getHeader(String key) {
        return headers.get(key);
    }

    /**
     * 添加头
     *
     * @param key   头键
     * @param value 头值
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }
}
