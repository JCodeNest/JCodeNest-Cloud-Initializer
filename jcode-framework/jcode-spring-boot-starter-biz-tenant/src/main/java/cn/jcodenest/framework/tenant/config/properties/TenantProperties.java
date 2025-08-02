package cn.jcodenest.framework.tenant.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 多租户配置
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@ConfigurationProperties(prefix = "jcode.tenant")
public class TenantProperties {

    /**
     * 租户是否开启, 默认开启
     */
    private static final Boolean ENABLE_DEFAULT = true;

    /**
     * 是否开启
     */
    private Boolean enable = ENABLE_DEFAULT;

    /**
     * 需要忽略多租户的请求
     * <p>
     * 默认情况下，每个请求需要带上 tenant-id 的请求头。但是部分请求是无需带上的，例如短信回调、支付回调等 Open API。
     */
    private Set<String> ignoreUrls = new HashSet<>();

    /**
     * 需要忽略跨（切换）租户访问的请求
     * <p>
     * 原因是：某些接口，访问的是个人信息，在跨租户是获取不到的。
     */
    private Set<String> ignoreVisitUrls = Collections.emptySet();

    /**
     * 需要忽略多租户的表
     * <p>
     * 即默认所有表都开启多租户的功能，所以记得添加对应的 tenant_id 字段。
     */
    private Set<String> ignoreTables = Collections.emptySet();

    /**
     * 需要忽略多租户的 Spring Cache 缓存
     * <p>
     * 即默认所有缓存都开启多租户的功能，所以记得添加对应的 tenant_id 字段。
     */
    private Set<String> ignoreCaches = Collections.emptySet();
}
