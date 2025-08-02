package cn.jcodenest.framework.mq.redis.core.interceptor;

import cn.jcodenest.framework.mq.redis.core.message.AbstractRedisMessage;

/**
 * {@link AbstractRedisMessage} 消息拦截器
 *
 * <p>
 * 通过拦截器，作为插件机制，实现拓展。例如多租户场景下的 MQ 消息处理。
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public interface RedisMessageInterceptor {

    /**
     * 发送消息之前进行拦截处理
     *
     * @param message 消息
     */
    default void sendMessageBefore(AbstractRedisMessage message) {
    }

    /**
     * 发送消息之后进行拦截处理
     *
     * @param message 消息
     */
    default void sendMessageAfter(AbstractRedisMessage message) {
    }

    /**
     * 接收消息之前进行拦截处理
     *
     * @param message 消息
     */
    default void consumeMessageBefore(AbstractRedisMessage message) {
    }

    /**
     * 接收消息之后进行拦截处理
     *
     * @param message 消息
     */
    default void consumeMessageAfter(AbstractRedisMessage message) {
    }
}
