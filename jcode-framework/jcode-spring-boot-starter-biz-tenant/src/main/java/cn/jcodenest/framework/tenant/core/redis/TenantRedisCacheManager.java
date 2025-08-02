package cn.jcodenest.framework.tenant.core.redis;

import cn.hutool.core.collection.CollUtil;
import cn.jcodenest.framework.redis.core.TimeoutRedisCacheManager;
import cn.jcodenest.framework.tenant.core.content.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.util.Set;

/**
 * 多租户的 {@link RedisCacheManager} 实现类
 * 操作指定 name 的 {@link Cache} 时，自动拼接租户后缀，格式为 name + ":" + tenantId + 后缀
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class TenantRedisCacheManager extends TimeoutRedisCacheManager {

    /**
     * 忽略的缓存名称
     */
    private final Set<String> ignoreCaches;

    /**
     * 构造器
     *
     * @param cacheWriter      缓存写入器
     * @param defaultCacheConfiguration 默认缓存配置
     * @param ignoreCaches     忽略的缓存名称
     */
    public TenantRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Set<String> ignoreCaches) {
        super(cacheWriter, defaultCacheConfiguration);
        this.ignoreCaches = ignoreCaches;
    }

    /**
     * 获取缓存
     *
     * @param name 缓存名称
     * @return 缓存
     */
    @Override
    public Cache getCache(String name) {
        // 如果开启多租户则 name 拼接租户后缀
        if (!TenantContextHolder.isIgnore() && TenantContextHolder.getTenantId() != null && !CollUtil.contains(ignoreCaches, name)) {
            name = name + ":" + TenantContextHolder.getTenantId();
        }

        // 继续基于父方法
        return super.getCache(name);
    }
}
