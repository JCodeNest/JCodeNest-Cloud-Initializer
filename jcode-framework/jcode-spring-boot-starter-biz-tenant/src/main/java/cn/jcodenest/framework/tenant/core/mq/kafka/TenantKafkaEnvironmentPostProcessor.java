package cn.jcodenest.framework.tenant.core.mq.kafka;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 多租户的 Kafka 的 {@link EnvironmentPostProcessor} 实现类
 * Kafka Producer 发送消息时，增加 {@link TenantKafkaProducerInterceptor} 拦截器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class TenantKafkaEnvironmentPostProcessor implements EnvironmentPostProcessor {

    /**
     * Kafka Producer Bean 名称
     */
    private static final String PROPERTY_KEY_INTERCEPTOR_CLASSES = "spring.kafka.producer.properties.interceptor.classes";

    /**
     * 在环境初始化后，添加 Kafka Producer 拦截器
     *
     * @param environment  环境
     * @param application  应用
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 添加 TenantKafkaProducerInterceptor 拦截器
        try {
            String value = environment.getProperty(PROPERTY_KEY_INTERCEPTOR_CLASSES);
            if (StrUtil.isEmpty(value)) {
                value = TenantKafkaProducerInterceptor.class.getName();
            } else {
                value += "," + TenantKafkaProducerInterceptor.class.getName();
            }
            environment.getSystemProperties().put(PROPERTY_KEY_INTERCEPTOR_CLASSES, value);
        } catch (NoClassDefFoundError ignore) {
            // 如果触发 NoClassDefFoundError 异常，说明 TenantKafkaProducerInterceptor 类不存在，即没引入 kafka-spring 依赖
        }
    }
}
