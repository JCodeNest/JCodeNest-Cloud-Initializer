package cn.jcodenest.framework.websocket.core.security;

import cn.jcodenest.framework.security.config.AuthorizeRequestsCustomizer;
import cn.jcodenest.framework.websocket.config.properties.WebSocketProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * WebSocket 的权限自定义
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@RequiredArgsConstructor
public class WebSocketAuthorizeRequestsCustomizer extends AuthorizeRequestsCustomizer {

    private final WebSocketProperties webSocketProperties;

    /**
     * 添加权限
     *
     * @param registry 权限注册
     */
    @Override
    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers(webSocketProperties.getPath()).permitAll();
    }
}
