package cn.jcodenest.framework.security.core.service;

/**
 * Security 框架 Service 接口, 定义权限相关校验操作
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public interface SecurityFrameworkService {

    /**
     * 判断是否有权限
     *
     * @param permission 权限
     * @return 是否
     */
    boolean hasPermission(String permission);

    /**
     * 判断是否有权限, 任一一个即可
     *
     * @param permissions 权限
     * @return 是否
     */
    boolean hasAnyPermissions(String... permissions);

    /**
     * 判断是否有角色
     * <p>
     * 注意: 角色使用的是 SysRoleDO 的 code 标识
     *
     * @param role 角色
     * @return 是否
     */
    boolean hasRole(String role);

    /**
     * 判断是否有角色, 任一一个即可
     *
     * @param roles 角色数组
     * @return 是否
     */
    boolean hasAnyRoles(String... roles);

    /**
     * 判断是否有授权
     *
     * @param scope 授权
     * @return 是否
     */
    boolean hasScope(String scope);

    /**
     * 判断是否有授权范围,, 任一一个即可
     *
     * @param scope 授权范围数组
     * @return 是否
     */
    boolean hasAnyScopes(String... scope);
}
