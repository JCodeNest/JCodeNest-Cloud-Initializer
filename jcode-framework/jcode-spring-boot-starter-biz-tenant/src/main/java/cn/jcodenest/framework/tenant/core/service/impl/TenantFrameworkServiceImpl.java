package cn.jcodenest.framework.tenant.core.service.impl;

import cn.jcodenest.framework.common.biz.system.tenant.TenantCommonApi;
import cn.jcodenest.framework.common.pojo.CommonResult;
import cn.jcodenest.framework.tenant.core.service.TenantFrameworkService;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.time.Duration;
import java.util.List;

import static cn.jcodenest.framework.common.util.cache.CacheUtils.buildAsyncReloadingCache;

/**
 * Tenant 框架 Service 实现类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@RequiredArgsConstructor
public class TenantFrameworkServiceImpl implements TenantFrameworkService {

    /**
     * 多租户通用 API
     */
    private final TenantCommonApi tenantApi;

    /**
     * 针对 {@link #getTenantIds()} 的缓存
     */
    private final LoadingCache<Object, List<Long>> getTenantIdsCache = buildAsyncReloadingCache(
            // 过期时间 1 分钟
            Duration.ofMinutes(1L),
            new CacheLoader<>() {
                @Override
                public List<Long> load(Object key) {
                    return tenantApi.getTenantIdList().getCheckedData();
                }
            });

    /**
     * 针对 {@link #validTenant(Long)} 的缓存
     */
    private final LoadingCache<Long, CommonResult<Boolean>> validTenantCache = buildAsyncReloadingCache(
            // 过期时间 1 分钟
            Duration.ofMinutes(1L),
            new CacheLoader<>() {
                @Override
                public CommonResult<Boolean> load(Long id) {
                    return tenantApi.validTenant(id);
                }
            });

    /**
     * 获得所有租户
     *
     * @return 租户编号数组
     */
    @Override
    @SneakyThrows
    public List<Long> getTenantIds() {
        return getTenantIdsCache.get(Boolean.TRUE);
    }

    /**
     * 校验租户是否合法
     *
     * @param id 租户编号
     */
    @Override
    @SneakyThrows
    public void validTenant(Long id) {
        validTenantCache.get(id).checkError();
    }
}
