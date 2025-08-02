package cn.jcodenest.framework.tenant.core.mq.rocketmq;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.tenant.core.content.TenantContextHolder;
import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import java.util.List;

import static cn.jcodenest.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * RocketMQ 消息队列的多租户 {@link ConsumeMessageHook} 实现类
 * Consumer 消费消息时，将消息的 Header 的租户编号添加到 {@link TenantContextHolder} 中，通过 {@link InvocableHandlerMethod} 实现
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class TenantRocketMQConsumeMessageHook implements ConsumeMessageHook {

    /**
     * 获取钩子名称
     *
     * @return 钩子名称
     */
    @Override
    public String hookName() {
        return getClass().getSimpleName();
    }

    /**
     * 消费消息之前，将租户ID设置到消息中
     *
     * @param context 消息上下文
     */
    @Override
    public void consumeMessageBefore(ConsumeMessageContext context) {
        // 校验消息必须是单条，不然设置租户可能不正确
        List<MessageExt> messages = context.getMsgList();
        Assert.isTrue(messages.size() == 1, "消息条数({})不正确", messages.size());

        // 设置租户编号
        String tenantId = messages.get(0).getUserProperty(HEADER_TENANT_ID);
        if (StrUtil.isNotEmpty(tenantId)) {
            TenantContextHolder.setTenantId(Long.parseLong(tenantId));
        }
    }

    /**
     * 消费消息后，清理租户上下文
     *
     * @param context 上下文
     */
    @Override
    public void consumeMessageAfter(ConsumeMessageContext context) {
        TenantContextHolder.clear();
    }
}
