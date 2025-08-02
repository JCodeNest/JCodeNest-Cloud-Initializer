package cn.jcodenest.framework.tenant.core.mq.redis;

import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.mq.redis.core.interceptor.RedisMessageInterceptor;
import cn.jcodenest.framework.mq.redis.core.message.AbstractRedisMessage;
import cn.jcodenest.framework.tenant.core.content.TenantContextHolder;

import static cn.jcodenest.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * 多租户 {@link AbstractRedisMessage} 拦截器
 *
 * <p>
 * 1. Producer 发送消息时，将 {@link TenantContextHolder} 租户编号，添加到消息的 Header 中
 * 2. Consumer 消费消息时，将消息的 Header 的租户编号，添加到 {@link TenantContextHolder} 中
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class TenantRedisMessageInterceptor implements RedisMessageInterceptor {

    /**
     * 发送消息之前进行拦截处理
     *
     * @param message 消息
     */
    @Override
    public void sendMessageBefore(AbstractRedisMessage message) {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            message.addHeader(HEADER_TENANT_ID, tenantId.toString());
        }
    }

    /**
     * 接收消息之前进行拦截处理
     *
     * @param message 消息
     */
    @Override
    public void consumeMessageBefore(AbstractRedisMessage message) {
        String tenantIdStr = message.getHeader(HEADER_TENANT_ID);
        if (StrUtil.isNotEmpty(tenantIdStr)) {
            TenantContextHolder.setTenantId(Long.valueOf(tenantIdStr));
        }
    }

    /**
     * 接收消息之后进行拦截处理
     *
     * @param message 消息
     */
    @Override
    public void consumeMessageAfter(AbstractRedisMessage message) {
        // 注意，Consumer 是一个逻辑的入口，所以不考虑原本上下文就存在租户编号的情况
        TenantContextHolder.clear();
    }
}
