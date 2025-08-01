package cn.jcodenest.framework.security.config;

import cn.jcodenest.framework.web.config.WebProperties;
import jakarta.annotation.Resource;
import org.springframework.core.Ordered;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * 自定义 URL 安全配置
 *
 * <p>目的：每个 Maven Module 可以自定义规则</p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public abstract class AuthorizeRequestsCustomizer implements Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>, Ordered {

    @Resource
    private WebProperties webProperties;

    /**
     * 获取顺序
     *
     * @return 顺序
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 构建 Admin API
     *
     * @param url URL
     * @return Admin API
     */
    protected String buildAdminApi(String url) {
        return webProperties.getAdminApi().getPrefix() + url;
    }

    /**
     * 构建 APP API
     *
     * @param url URL
     * @return APP API
     */
    protected String buildAppApi(String url) {
        return webProperties.getAppApi().getPrefix() + url;
    }
}
