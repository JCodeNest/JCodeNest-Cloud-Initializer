package cn.jcodenest.wiki.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Web 过滤器顺序枚举
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebFilterOrderConstants {

    /**
     * CORS 过滤器顺序
     */
    public static final int CORS_FILTER = Integer.MIN_VALUE;

    /**
     * Trace 过滤器顺序
     */
    public static final int TRACE_FILTER = CORS_FILTER + 1;

    /**
     * EnvTag 过滤器顺序
     */
    public static final int ENV_TAG_FILTER = TRACE_FILTER + 1;

    /**
     * RequestBody 缓存过滤器顺序
     */
    int REQUEST_BODY_CACHE_FILTER = Integer.MIN_VALUE + 500;

    // OrderedRequestContextFilter 默认为 -105, 用于国际化上下文等等

    /**
     * 租户上下文过滤器顺序
     * 注意：需要保证在 ApiAccessLogFilter 前面
     */
    public static final int TENANT_CONTEXT_FILTER = -104;

    /**
     * API 访问日志过滤器顺序
     * 注意：需要保证在 RequestBodyCacheFilter 后面
     */
    public static final int API_ACCESS_LOG_FILTER = -103;

    /**
     * XSS 过滤器顺序
     * 注意：需要保证在 RequestBodyCacheFilter 后面
     */
    public static final int XSS_FILTER = -102;

    // Spring Security Filter 默认为 -100
    // see org.springframework.boot.autoconfigure.security.SecurityProperties

    /**
     * 租户安全过滤器顺序
     * 注意：需要保证在 Spring Security 过滤器后面
     */
    public static final int TENANT_SECURITY_FILTER = -99;


    /**
     * Flowable 过滤器顺序
     * 注意：需要保证在 Spring Security 过滤后面
     */
    public static final int FLOWABLE_FILTER = -98;

    /**
     * 演示模式过滤器顺序
     * 注意：需要保证在 Spring Security 过滤后面
     */
    public static final int DEMO_FILTER = Integer.MAX_VALUE;
}
