package cn.jcodenest.framework.tenant.core.mq.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 多租户的 RabbitMQ 初始化器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class TenantRabbitMQInitializer implements BeanPostProcessor {

    /**
     * 初始化 RabbitMQ
     *
     * @param bean     对象
     * @param beanName 对象名
     * @return 对象
     * @throws BeansException 异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RabbitTemplate rabbitTemplate) {
            rabbitTemplate.addBeforePublishPostProcessors(new TenantRabbitMQMessagePostProcessor());
        }
        return bean;
    }
}
