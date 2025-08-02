package cn.jcodenest.framework.env.config;

import cn.jcodenest.framework.common.constants.WebFilterOrderConstants;
import cn.jcodenest.framework.env.config.properties.EnvProperties;
import cn.jcodenest.framework.env.core.web.EnvWebFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 多环境的 Web 组件自动配置
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(EnvProperties.class)
public class JCodeEnvWebAutoConfiguration {

    /**
     * 创建 {@link EnvWebFilter} Bean
     */
    @Bean
    public FilterRegistrationBean<EnvWebFilter> envWebFilterFilter() {
        EnvWebFilter filter = new EnvWebFilter();
        FilterRegistrationBean<EnvWebFilter> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(WebFilterOrderConstants.ENV_TAG_FILTER);
        return bean;
    }
}
