package cn.jcodenest.framework.datapermission.core.util;

import cn.jcodenest.framework.datapermission.core.aop.DataPermissionContextHolder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * DataPermissionUtils 测试类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
class DataPermissionUtilsTest {

    @Test
    void testExecuteIgnore() {
        DataPermissionUtils.executeIgnore(() -> assertFalse(DataPermissionContextHolder.get().enable()));
    }
}
