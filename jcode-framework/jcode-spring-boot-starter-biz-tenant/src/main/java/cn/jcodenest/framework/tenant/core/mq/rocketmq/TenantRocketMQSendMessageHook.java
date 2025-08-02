package cn.jcodenest.framework.tenant.core.mq.rocketmq;

import cn.jcodenest.framework.tenant.core.content.TenantContextHolder;
import org.apache.rocketmq.client.hook.SendMessageContext;
import org.apache.rocketmq.client.hook.SendMessageHook;

import static cn.jcodenest.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * RocketMQ 消息队列的多租户 {@link SendMessageHook} 实现类
 * Producer 发送消息时，将 {@link TenantContextHolder} 租户编号，添加到消息的 Header 中
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class TenantRocketMQSendMessageHook implements SendMessageHook {

    /**
     * 获取钩子名称
     */
    @Override
    public String hookName() {
        return getClass().getSimpleName();
    }

    /**
     * 发送消息之前, 添加租户编号的 Header
     *
     * @param sendMessageContext 上下文
     */
    @Override
    public void sendMessageBefore(SendMessageContext sendMessageContext) {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            return;
        }
        sendMessageContext.getMessage().putUserProperty(HEADER_TENANT_ID, tenantId.toString());
    }

    /**
     * 发送消息之后
     *
     * @param sendMessageContext 上下文
     */
    @Override
    public void sendMessageAfter(SendMessageContext sendMessageContext) {
    }
}
