package cn.jcodenest.framework.mq.redis.core.pubsub;

import cn.jcodenest.framework.mq.redis.core.message.AbstractRedisMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Redis Channel Message 抽象类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public abstract class AbstractRedisChannelMessage extends AbstractRedisMessage {

    /**
     * 获取 Redis Channel，默认使用类名
     *
     * @return Channel 名称
     */
    @JsonIgnore // 避免序列化，原因是 Redis 发布 Channel 消息的时候已经会指定。
    public String getChannel() {
        return getClass().getSimpleName();
    }
}
