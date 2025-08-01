package cn.jcodenest.framework.security.core.service;

import cn.hutool.core.collection.CollUtil;
import cn.jcodenest.framework.common.biz.system.permission.PermissionCommonApi;
import cn.jcodenest.framework.common.core.KeyValue;
import cn.jcodenest.framework.common.util.cache.CacheUtils;
import cn.jcodenest.framework.security.core.LoginUser;
import cn.jcodenest.framework.security.core.util.SecurityFrameworkUtils;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * 默认的 {@link SecurityFrameworkService} 实现类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AllArgsConstructor
public class SecurityFrameworkServiceImpl implements SecurityFrameworkService {

    private final PermissionCommonApi permissionApi;

    /**
     * 针对 {@link #hasAnyRoles(String...)} 的缓存
     */
    private final LoadingCache<KeyValue<Long, List<String>>, Boolean> hasAnyRolesCache = CacheUtils.buildCache(
            // 过期时间 1 分钟
            Duration.ofMinutes(1L),
            new CacheLoader<>() {

                @Override
                public Boolean load(KeyValue<Long, List<String>> key) {
                    return permissionApi.hasAnyRoles(key.getKey(), key.getValue().toArray(new String[0])).getCheckedData();
                }
            });

    /**
     * 针对 {@link #hasAnyPermissions(String...)} 的缓存
     */
    private final LoadingCache<KeyValue<Long, List<String>>, Boolean> hasAnyPermissionsCache = CacheUtils.buildCache(
            // 过期时间 1 分钟
            Duration.ofMinutes(1L),
            new CacheLoader<>() {

                @Override
                public Boolean load(KeyValue<Long, List<String>> key) {
                    return permissionApi.hasAnyPermissions(key.getKey(), key.getValue().toArray(new String[0])).getCheckedData();
                }
            });

    /**
     * 判断是否有权限
     *
     * @param permission 权限
     * @return 是否
     */
    @Override
    public boolean hasPermission(String permission) {
        return hasAnyPermissions(permission);
    }

    /**
     * 判断是否有权限, 任一一个即可
     *
     * @param permissions 权限
     * @return 是否
     */
    @Override
    @SneakyThrows
    public boolean hasAnyPermissions(String... permissions) {
        // 特殊：跨租户访问
        if (SecurityFrameworkUtils.skipPermissionCheck()) {
            return true;
        }

        // 权限校验
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            return false;
        }

        return hasAnyPermissionsCache.get(new KeyValue<>(userId, Arrays.asList(permissions)));
    }

    /**
     * 判断是否有角色
     * <p>
     * 注意: 角色使用的是 SysRoleDO 的 code 标识
     *
     * @param role 角色
     * @return 是否
     */
    @Override
    public boolean hasRole(String role) {
        return hasAnyRoles(role);
    }

    /**
     * 判断是否有角色, 任一一个即可
     *
     * @param roles 角色数组
     * @return 是否
     */
    @Override
    @SneakyThrows
    public boolean hasAnyRoles(String... roles) {
        // 特殊：跨租户访问
        if (SecurityFrameworkUtils.skipPermissionCheck()) {
            return true;
        }

        // 权限校验
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            return false;
        }

        return hasAnyRolesCache.get(new KeyValue<>(userId, Arrays.asList(roles)));
    }

    /**
     * 判断是否有授权
     *
     * @param scope 授权
     * @return 是否
     */
    @Override
    public boolean hasScope(String scope) {
        return hasAnyScopes(scope);
    }

    /**
     * 判断是否有授权范围,, 任一一个即可
     *
     * @param scope 授权范围数组
     * @return 是否
     */
    @Override
    public boolean hasAnyScopes(String... scope) {
        // 特殊：跨租户访问
        if (SecurityFrameworkUtils.skipPermissionCheck()) {
            return true;
        }

        // 权限校验
        LoginUser user = SecurityFrameworkUtils.getLoginUser();
        if (user == null) {
            return false;
        }

        return CollUtil.containsAny(user.getScopes(), Arrays.asList(scope));
    }
}
