package cn.jcodenest.framework.datapermission.core.rpc;

import cn.jcodenest.framework.datapermission.core.annotation.DataPermission;
import cn.jcodenest.framework.datapermission.core.aop.DataPermissionContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * DataPermission 的 RequestInterceptor 实现类：
 * Feign 请求时，将 {@link DataPermission} 设置到 header 中，继续透传给被调用的服务
 * 注意：由于 {@link DataPermission} 不支持序列化和反序列化，所以暂时只能传递它的 enable 属性
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class DataPermissionRequestInterceptor implements RequestInterceptor {

    /**
     * 透传的 Header 名
     */
    public static final String ENABLE_HEADER_NAME = "data-permission-enable";

    /**
     * 拦截 Feign 请求，将 {@link DataPermission} 设置到 header 中
     *
     * @param requestTemplate 请求模板
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        DataPermission dataPermission = DataPermissionContextHolder.get();
        if (dataPermission != null && !dataPermission.enable()) {
            requestTemplate.header(ENABLE_HEADER_NAME, "false");
        }
    }
}
