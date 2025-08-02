package cn.jcodenest.framework.datapermission.core.aop;

import cn.jcodenest.framework.datapermission.core.annotation.DataPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

/**
 * {@link DataPermissionContextHolder} 的单元测试
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
class DataPermissionContextHolderTest {

    @BeforeEach
    void setUp() {
        DataPermissionContextHolder.clear();
    }

    @Test
    void testGet() {
        // mock 方法
        DataPermission dataPermission01 = mock(DataPermission.class);
        DataPermissionContextHolder.add(dataPermission01);
        DataPermission dataPermission02 = mock(DataPermission.class);
        DataPermissionContextHolder.add(dataPermission02);

        // 调用
        DataPermission result = DataPermissionContextHolder.get();
        // 断言
        assertSame(result, dataPermission02);
    }

    @Test
    void testPush() {
        // 调用
        DataPermission dataPermission01 = mock(DataPermission.class);
        DataPermissionContextHolder.add(dataPermission01);
        DataPermission dataPermission02 = mock(DataPermission.class);
        DataPermissionContextHolder.add(dataPermission02);

        // 断言
        DataPermission first = DataPermissionContextHolder.getAll().get(0);
        DataPermission second = DataPermissionContextHolder.getAll().get(1);
        assertSame(dataPermission01, first);
        assertSame(dataPermission02, second);
    }

    @Test
    void testRemove() {
        // mock 方法
        DataPermission dataPermission01 = mock(DataPermission.class);
        DataPermissionContextHolder.add(dataPermission01);
        DataPermission dataPermission02 = mock(DataPermission.class);
        DataPermissionContextHolder.add(dataPermission02);

        // 调用
        DataPermission result = DataPermissionContextHolder.remove();

        // 断言
        assertSame(result, dataPermission02);
        assertEquals(1, DataPermissionContextHolder.getAll().size());
    }
}
