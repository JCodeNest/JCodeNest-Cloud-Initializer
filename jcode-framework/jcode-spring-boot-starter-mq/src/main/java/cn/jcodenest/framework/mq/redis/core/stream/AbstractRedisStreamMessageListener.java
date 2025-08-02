package cn.jcodenest.framework.mq.redis.core.stream;

import cn.hutool.core.util.TypeUtil;
import cn.jcodenest.framework.common.util.json.JsonUtils;
import cn.jcodenest.framework.mq.redis.core.RedisMQTemplate;
import cn.jcodenest.framework.mq.redis.core.interceptor.RedisMessageInterceptor;
import cn.jcodenest.framework.mq.redis.core.message.AbstractRedisMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Redis Stream 监听器抽象类，用于实现集群消费
 *
 * @param <T> 消息类型，一定要填写，不然会报错
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public abstract class AbstractRedisStreamMessageListener<T extends AbstractRedisStreamMessage>
        implements StreamListener<String, ObjectRecord<String, String>> {

    /**
     * 消息类型
     */
    private final Class<T> messageType;

    /**
     * Redis Channel
     */
    @Getter
    private final String streamKey;

    /**
     * Redis 消费者分组，默认使用 spring.application.name 名字
     */
    @Value("${spring.application.name}")
    @Getter
    private String group;

    /**
     * RedisMQTemplate
     */
    @Setter
    private RedisMQTemplate redisMQTemplate;

    /**
     * 默认构造器
     */
    @SneakyThrows
    protected AbstractRedisStreamMessageListener() {
        this.messageType = getMessageClass();
        this.streamKey = messageType.getDeclaredConstructor().newInstance().getStreamKey();
    }

    /**
     * 监听到消息时，会调用此方法
     *
     * <P>
     *     TODO：需要额外考虑以下几个点：
     *     1. 处理异常的情况
     *     2. 发送日志；以及事务的结合
     *     3. 消费日志；以及通用的幂等性
     *     4. 消费失败的重试，https://zhuanlan.zhihu.com/p/60501638
     * </P>
     *
     * @param message never {@literal null}.
     */
    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        // 消费消息
        T messageObj = JsonUtils.parseObject(message.getValue(), messageType);
        try {
            // 消费消息之前调用拦截器
            consumeMessageBefore(messageObj);
            // 消费消息
            this.onMessage(messageObj);
            // ack 消息消费完成
            redisMQTemplate.getRedisTemplate().opsForStream().acknowledge(group, message);
        } finally {
            // 消费消息之后调用拦截器
            consumeMessageAfter(messageObj);
        }
    }

    /**
     * 处理消息
     *
     * @param message 消息
     */
    public abstract void onMessage(T message);

    /**
     * 通过解析类上的泛型，获得消息类型
     *
     * @return 消息类型
     */
    @SuppressWarnings("unchecked")
    private Class<T> getMessageClass() {
        Type type = TypeUtil.getTypeArgument(getClass(), 0);
        if (type == null) {
            throw new IllegalStateException(String.format("类型(%s) 需要设置消息类型", getClass().getName()));
        }
        return (Class<T>) type;
    }

    /**
     * 消费消息之前调用拦截器
     *
     * @param message 消息
     */
    private void consumeMessageBefore(AbstractRedisMessage message) {
        assert redisMQTemplate != null;
        List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();
        // 正序处理拦截器
        interceptors.forEach(interceptor -> interceptor.consumeMessageBefore(message));
    }

    /**
     * 消费消息之后调用拦截器
     *
     * @param message 消息
     */
    private void consumeMessageAfter(AbstractRedisMessage message) {
        assert redisMQTemplate != null;
        List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();
        // 倒序处理拦截器
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).consumeMessageAfter(message);
        }
    }
}
