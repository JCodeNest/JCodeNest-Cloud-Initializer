package cn.jcodenest.framework.mq.redis.core;

import cn.jcodenest.framework.common.util.json.JsonUtils;
import cn.jcodenest.framework.mq.redis.core.interceptor.RedisMessageInterceptor;
import cn.jcodenest.framework.mq.redis.core.message.AbstractRedisMessage;
import cn.jcodenest.framework.mq.redis.core.pubsub.AbstractRedisChannelMessage;
import cn.jcodenest.framework.mq.redis.core.stream.AbstractRedisStreamMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis MQ 操作模板类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AllArgsConstructor
public class RedisMQTemplate {

    /**
     * Redis 模板
     */
    @Getter
    private final RedisTemplate<String, ?> redisTemplate;

    /**
     * 拦截器数组
     */
    @Getter
    private final List<RedisMessageInterceptor> interceptors = new ArrayList<>();

    /**
     * 发送 Redis 消息，基于 Redis pub/sub 实现
     *
     * @param message 消息
     */
    public <T extends AbstractRedisChannelMessage> void send(T message) {
        try {
             // 发送消息之前调用拦截器
            sendMessageBefore(message);
            // 发送消息
            redisTemplate.convertAndSend(message.getChannel(), JsonUtils.toJsonString(message));
        } finally {
             // 发送消息之后调用拦截器
            sendMessageAfter(message);
        }
    }

    /**
     * 发送 Redis 消息，基于 Redis Stream 实现
     *
     * @param message 消息
     * @return 消息记录的编号对象
     */
    public <T extends AbstractRedisStreamMessage> RecordId send(T message) {
        try {
            // 发送消息之前调用拦截器
            sendMessageBefore(message);
            // 发送消息: 设置内容、设置 stream key
            return redisTemplate.opsForStream().add(StreamRecords.newRecord()
                    .ofObject(JsonUtils.toJsonString(message))
                    .withStreamKey(message.getStreamKey()));
        } finally {
             // 发送消息之后调用拦截器
            sendMessageAfter(message);
        }
    }

    /**
     * 添加拦截器
     *
     * @param interceptor 拦截器
     */
    public void addInterceptor(RedisMessageInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * 发送消息之前
     *
     * @param message 消息
     */
    private void sendMessageBefore(AbstractRedisMessage message) {
        // 正序处理拦截器
        interceptors.forEach(interceptor -> interceptor.sendMessageBefore(message));
    }

    /**
     * 发送消息之后
     *
     * @param message 消息
     */
    private void sendMessageAfter(AbstractRedisMessage message) {
        // 倒序处理拦截器
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).sendMessageAfter(message);
        }
    }
}
