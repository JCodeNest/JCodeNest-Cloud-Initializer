package cn.jcodenest.framework.env.core.feign;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;

/**
 * 多环境的 {@link LoadBalancerClientFactory} 实现类
 * 在创建 {@link ReactiveLoadBalancer} 时，会额外增加 {@link EnvLoadBalancerClient} 代理，用于 tag 过滤服务实例
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class EnvLoadBalancerClientFactory extends LoadBalancerClientFactory {

    /**
     * 构造函数
     *
     * @param properties 负载均衡客户端属性
     */
    public EnvLoadBalancerClientFactory(LoadBalancerClientsProperties properties) {
        super(properties);
    }

    /**
     * 重写 getInstance 方法
     *
     * @param serviceId 服务 ID
     * @return 负载均衡客户端
     */
    @Override
    public ReactiveLoadBalancer<ServiceInstance> getInstance(String serviceId) {
        ReactiveLoadBalancer<ServiceInstance> reactiveLoadBalancer = super.getInstance(serviceId);
        // 参考 {@link com.alibaba.cloud.nacos.loadbalancer.NacosLoadBalancerClientConfiguration#nacosLoadBalancer(Environment, LoadBalancerClientFactory, NacosDiscoveryProperties)} 方法
        return new EnvLoadBalancerClient(super.getLazyProvider(serviceId, ServiceInstanceListSupplier.class), serviceId, reactiveLoadBalancer);
    }
}
