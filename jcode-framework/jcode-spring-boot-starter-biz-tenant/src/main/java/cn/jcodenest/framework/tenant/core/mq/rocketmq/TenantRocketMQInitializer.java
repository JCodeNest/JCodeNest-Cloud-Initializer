package cn.jcodenest.framework.tenant.core.mq.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl;
import org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 多租户的 RocketMQ 初始化器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class TenantRocketMQInitializer implements BeanPostProcessor {

    /**
     * 初始化 RocketMQ 配置
     *
     * @param bean     the bean to be processed
     * @param beanName the name of the bean
     * @return the bean to be registered
     * @throws BeansException if any error occurs
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DefaultRocketMQListenerContainer container) {
            initTenantConsumer(container.getConsumer());
        } else if (bean instanceof RocketMQTemplate template) {
            initTenantProducer(template.getProducer());
        }
        return bean;
    }

    /**
     * 初始化 RocketMQ 多租户生成者
     *
     * @param producer 生产者
     */
    private void initTenantProducer(DefaultMQProducer producer) {
        if (producer == null) {
            return;
        }

        DefaultMQProducerImpl producerImpl = producer.getDefaultMQProducerImpl();
        if (producerImpl == null) {
            return;
        }

        producerImpl.registerSendMessageHook(new TenantRocketMQSendMessageHook());
    }

    /**
     * 初始化消费者
     *
     * @param consumer 消费者
     */
    private void initTenantConsumer(DefaultMQPushConsumer consumer) {
        if (consumer == null) {
            return;
        }

        DefaultMQPushConsumerImpl consumerImpl = consumer.getDefaultMQPushConsumerImpl();
        if (consumerImpl == null) {
            return;
        }

        consumerImpl.registerConsumeMessageHook(new TenantRocketMQConsumeMessageHook());
    }
}
