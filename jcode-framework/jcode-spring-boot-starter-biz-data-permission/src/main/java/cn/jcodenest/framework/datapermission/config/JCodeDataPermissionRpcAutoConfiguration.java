package cn.jcodenest.framework.datapermission.config;

import cn.jcodenest.framework.datapermission.core.rpc.DataPermissionRequestInterceptor;
import cn.jcodenest.framework.datapermission.core.rpc.DataPermissionRpcWebFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import static cn.jcodenest.framework.common.constants.WebFilterOrderConstants.TENANT_CONTEXT_FILTER;

/**
 * 数据权限针对 RPC 的自动配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
@ConditionalOnClass(name = "feign.RequestInterceptor")
public class JCodeDataPermissionRpcAutoConfiguration {

    /**
     * 创建 Feign 请求拦截器，用于数据权限
     *
     * @return 拦截器
     */
    @Bean
    public DataPermissionRequestInterceptor dataPermissionRequestInterceptor() {
        return new DataPermissionRequestInterceptor();
    }

    /**
     * 创建 Feign Web 过滤器，用于数据权限
     *
     * @return 过滤器
     */
    @Bean
    public FilterRegistrationBean<DataPermissionRpcWebFilter> dataPermissionRpcFilter() {
        FilterRegistrationBean<DataPermissionRpcWebFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new DataPermissionRpcWebFilter());
        // 顺序没有绝对的要求，在租户 Filter 前面稳妥点
        registrationBean.setOrder(TENANT_CONTEXT_FILTER - 1);
        return registrationBean;
    }
}
