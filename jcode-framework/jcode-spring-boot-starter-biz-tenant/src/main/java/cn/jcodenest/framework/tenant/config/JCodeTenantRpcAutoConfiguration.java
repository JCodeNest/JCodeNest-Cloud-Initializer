package cn.jcodenest.framework.tenant.config;

import cn.jcodenest.framework.common.biz.system.tenant.TenantCommonApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 多租户 RPC 自动配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
// 允许使用 jcode.tenant.enable=false 禁用多租户
@ConditionalOnProperty(prefix = "jcode.tenant", value = "enable", matchIfMissing = true)
// 主要是引入相关的 API 服务
@EnableFeignClients(clients = TenantCommonApi.class)
public class JCodeTenantRpcAutoConfiguration {
}
