package cn.jcodenest.framework.env.config;

import cn.jcodenest.framework.env.config.properties.EnvProperties;
import cn.jcodenest.framework.env.core.feign.EnvLoadBalancerClientFactory;
import cn.jcodenest.framework.env.core.feign.EnvRequestInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientSpecification;
import org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.List;

/**
 * 多环境的 RPC 组件自动配置
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
@EnableConfigurationProperties(EnvProperties.class)
public class JCodeEnvRpcAutoConfiguration {

    // ========== Feign 相关 ==========

    /**
     * 创建 {@link EnvLoadBalancerClientFactory} Bean
     * <p>
     * 参考 {@link LoadBalancerAutoConfiguration#loadBalancerClientFactory(LoadBalancerClientsProperties)} 方法
     */
    @Bean
    public LoadBalancerClientFactory loadBalancerClientFactory(LoadBalancerClientsProperties properties, ObjectProvider<List<LoadBalancerClientSpecification>> configurations) {
        EnvLoadBalancerClientFactory clientFactory = new EnvLoadBalancerClientFactory(properties);
        clientFactory.setConfigurations(configurations.getIfAvailable(Collections::emptyList));
        return clientFactory;
    }

    /**
     * 创建 {@link EnvRequestInterceptor} Bean
     */
    @Bean
    public EnvRequestInterceptor envRequestInterceptor() {
        return new EnvRequestInterceptor();
    }
}
