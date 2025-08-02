package cn.jcodenest.framework.ip.core.util;

import cn.jcodenest.framework.ip.core.Area;
import org.junit.jupiter.api.Test;
import org.lionsoul.ip2region.xdb.Searcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link IPUtils} 单元测试
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
class IPUtilsTest {

    @Test
    void testGetAreaId_string() {
        // 120.202.4.0|120.202.4.255|420600
        Integer areaId = IPUtils.getAreaId("120.202.4.50");
        assertEquals(420600, areaId);
    }

    @Test
    void testGetAreaId_long() throws Exception {
        // 120.203.123.0|120.203.133.255|360900
        long ip = Searcher.checkIP("120.203.123.250");
        Integer areaId = IPUtils.getAreaId(ip);
        assertEquals(360900, areaId);
    }

    @Test
    void testGetArea_string() {
        // 120.202.4.0|120.202.4.255|420600
        Area area = IPUtils.getArea("120.202.4.50");
        assertEquals("襄阳市", area.getName());
    }

    @Test
    void testGetArea_long() throws Exception {
        // 120.203.123.0|120.203.133.255|360900
        long ip = Searcher.checkIP("120.203.123.252");
        Area area = IPUtils.getArea(ip);
        assertEquals("宜春市", area.getName());
    }
}
