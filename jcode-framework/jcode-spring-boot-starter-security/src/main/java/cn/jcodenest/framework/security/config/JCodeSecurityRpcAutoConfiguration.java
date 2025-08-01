package cn.jcodenest.framework.security.config;

import cn.jcodenest.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import cn.jcodenest.framework.common.biz.system.permission.PermissionCommonApi;
import cn.jcodenest.framework.security.core.rpc.LoginUserRequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * Security 使用到 Feign 的配置项, 主要是引入相关的 API 服务
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
@EnableFeignClients(clients = {OAuth2TokenCommonApi.class, PermissionCommonApi.class})
public class JCodeSecurityRpcAutoConfiguration {

    @Bean
    public LoginUserRequestInterceptor loginUserRequestInterceptor() {
        return new LoginUserRequestInterceptor();
    }
}
