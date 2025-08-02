package cn.jcodenest.framework.datapermission.core.util;

import cn.jcodenest.framework.datapermission.core.annotation.DataPermission;
import cn.jcodenest.framework.datapermission.core.aop.DataPermissionContextHolder;
import lombok.SneakyThrows;

import java.util.concurrent.Callable;

/**
 * 数据权限工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class DataPermissionUtils {

    /**
     * 禁用数据权限
     */
    private static DataPermission DATA_PERMISSION_DISABLE;

    /**
     * 获取禁用数据权限
     *
     * @return 禁用数据权限
     */
    @DataPermission(enable = false)
    @SneakyThrows
    private static DataPermission getDisableDataPermissionDisable() {
        if (DATA_PERMISSION_DISABLE == null) {
            DATA_PERMISSION_DISABLE = DataPermissionUtils.class
                    .getDeclaredMethod("getDisableDataPermissionDisable")
                    .getAnnotation(DataPermission.class);
        }
        return DATA_PERMISSION_DISABLE;
    }

    /**
     * 忽略数据权限，执行对应的逻辑
     *
     * @param runnable 逻辑
     */
    public static void executeIgnore(Runnable runnable) {
        // 添加忽略数据权限
        addDisableDataPermission();
        try {
            // 执行 runnable
            runnable.run();
        } finally {
            // 移除忽略数据权限
            removeDataPermission();
        }
    }

    /**
     * 忽略数据权限，执行对应的逻辑
     *
     * @param callable 逻辑
     * @return 执行结果
     */
    @SneakyThrows
    public static <T> T executeIgnore(Callable<T> callable) {
        // 添加忽略数据权限
        addDisableDataPermission();
        try {
            // 执行 callable
            return callable.call();
        } finally {
            // 移除忽略数据权限
            removeDataPermission();
        }
    }

    /**
     * 添加忽略数据权限
     */
    public static void addDisableDataPermission() {
        DataPermission dataPermission = getDisableDataPermissionDisable();
        DataPermissionContextHolder.add(dataPermission);
    }

    /**
     * 移除数据权限
     */
    public static void removeDataPermission() {
        DataPermissionContextHolder.remove();
    }
}
