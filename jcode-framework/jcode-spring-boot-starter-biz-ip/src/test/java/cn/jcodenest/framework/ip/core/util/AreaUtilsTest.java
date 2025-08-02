package cn.jcodenest.framework.ip.core.util;

import cn.jcodenest.framework.ip.core.Area;
import cn.jcodenest.framework.ip.core.enums.AreaTypeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link AreaUtils} 单元测试
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
class AreaUtilsTest {

    @Test
    void testGetArea() {
        // 调用：北京
        Area area = AreaUtils.getArea(110100);

        // 断言
        assertEquals(110100, area.getId());
        assertEquals("北京市", area.getName());
        assertEquals(area.getType(), AreaTypeEnum.CITY.getType());
        assertEquals(110000, area.getParent().getId());
        assertEquals(16, area.getChildren().size());
    }

    @Test
    void testFormat() {
        assertEquals("北京市 北京市 朝阳区", AreaUtils.format(110105));
        assertEquals("中国", AreaUtils.format(1));
        assertEquals("蒙古", AreaUtils.format(2));
    }
}
