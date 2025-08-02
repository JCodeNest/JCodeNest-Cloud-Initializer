package cn.jcodenest.framework.tenant.core.mq.kafka;

import cn.hutool.core.util.ReflectUtil;
import cn.jcodenest.framework.tenant.core.content.TenantContextHolder;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import java.util.Map;

import static cn.jcodenest.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Kafka 消息队列的多租户 {@link ProducerInterceptor} 实现类
 *
 * <p>
 * 1. Producer 发送消息时，将 {@link TenantContextHolder} 租户编号，添加到消息的 Header 中
 * 2. Consumer 消费消息时，将消息的 Header 的租户编号，添加到 {@link TenantContextHolder} 中，通过 {@link InvocableHandlerMethod} 实现
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class TenantKafkaProducerInterceptor implements ProducerInterceptor<Object, Object> {

    /**
     * 发送消息之前
     *
     * @param record 消息
     * @return 拦截结果
     */
    @Override
    public ProducerRecord<Object, Object> onSend(ProducerRecord<Object, Object> record) {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            // private 属性没有 get 方法，智能反射
            Headers headers = (Headers) ReflectUtil.getFieldValue(record, "headers");
            headers.add(HEADER_TENANT_ID, tenantId.toString().getBytes());
        }
        return record;
    }

    /**
     * 在 ACK 确认消息时
     *
     * @param metadata 元数据
     * @param exception 异常
     */
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
    }

    /**
     * 关闭
     */
    @Override
    public void close() {
    }

    /**
     * 配置
     *
     * @param configs 配置
     */
    @Override
    public void configure(Map<String, ?> configs) {
    }
}
