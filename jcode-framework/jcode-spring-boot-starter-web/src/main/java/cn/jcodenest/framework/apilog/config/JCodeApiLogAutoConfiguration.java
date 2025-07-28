package cn.jcodenest.framework.apilog.config;

import cn.jcodenest.framework.apilog.core.filter.ApiAccessLogFilter;
import cn.jcodenest.framework.apilog.core.interceptor.ApiAccessLogInterceptor;
import cn.jcodenest.framework.common.biz.infra.logger.ApiAccessLogCommonApi;
import cn.jcodenest.framework.common.constants.WebFilterOrderConstants;
import cn.jcodenest.framework.web.config.JCodeWebAutoConfiguration;
import cn.jcodenest.framework.web.config.WebProperties;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API 日志自动配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/28
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration(after = JCodeWebAutoConfiguration.class) // 需要在 JCodeWebAutoConfiguration 之后加载
public class JCodeApiLogAutoConfiguration implements WebMvcConfigurer {

    /**
     * API 访问日志过滤器
     * 允许使用 jcode.access-log.enable=false 禁用访问日志
     *
     * @param webProperties   Web 配置
     * @param applicationName 应用名称
     * @param apiAccessLogApi API 访问日志通用 API
     * @return API 访问日志过滤器
     */
    @Bean
    @ConditionalOnProperty(prefix = "yudao.access-log", value = "enable", matchIfMissing = true)
    public FilterRegistrationBean<ApiAccessLogFilter> apiAccessLogFilter(WebProperties webProperties,
        @Value("${spring.application.name}") String applicationName, ApiAccessLogCommonApi apiAccessLogApi) {
        ApiAccessLogFilter filter = new ApiAccessLogFilter(webProperties, applicationName, apiAccessLogApi);
        return createFilterBean(filter);
    }

    /**
     * 创建过滤器 Bean
     *
     * @param filter 过滤器
     * @return 过滤器 Bean
     */
    private static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(WebFilterOrderConstants.API_ACCESS_LOG_FILTER);
        return bean;
    }

    /**
     * 添加 API 访问日志拦截器
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiAccessLogInterceptor());
    }
}
