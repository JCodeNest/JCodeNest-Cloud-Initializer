package cn.jcodenest.framework.tenant.core.rpc;

import cn.jcodenest.framework.tenant.core.content.TenantContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import static cn.jcodenest.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Tenant 的 RequestInterceptor 实现类
 * Feign 请求时，将 {@link TenantContextHolder} 设置到 header 中，继续透传给被调用的服务
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class TenantRequestInterceptor implements RequestInterceptor {

    /**
     * 拦截请求，将 {@link TenantContextHolder} 设置到 header 中
     *
     * @param requestTemplate 请求模板
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            requestTemplate.header(HEADER_TENANT_ID, String.valueOf(tenantId));
        }
    }
}
