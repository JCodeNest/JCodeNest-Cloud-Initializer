package cn.jcodenest.framework.excel.core.util;

import cn.jcodenest.framework.common.biz.system.dict.DictDataCommonApi;
import cn.jcodenest.framework.common.biz.system.dict.dto.DictDataRespDTO;
import cn.jcodenest.framework.common.pojo.CommonResult;
import cn.jcodenest.framework.dict.core.util.DictFrameworkUtils;
import cn.jcodenest.framework.test.core.ut.BaseMockitoUnitTest;
import cn.jcodenest.framework.test.core.util.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * {@link DictFrameworkUtils} 的单元测试
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class DictFrameworkUtilsTest extends BaseMockitoUnitTest {

    @Mock
    private DictDataCommonApi dictDataApi;

    @BeforeEach
    public void setUp() {
        DictFrameworkUtils.init(dictDataApi);
        DictFrameworkUtils.clearCache();
    }

    @Test
    public void testParseDictDataLabel() {
        // mock 数据
        List<DictDataRespDTO> dictDatas = List.of(
                RandomUtils.randomPojo(DictDataRespDTO.class, o -> o.setDictType("animal").setValue("cat").setLabel("猫")),
                RandomUtils.randomPojo(DictDataRespDTO.class, o -> o.setDictType("animal").setValue("dog").setLabel("狗"))
        );

        // mock 方法
        when(dictDataApi.getDictDataList(eq("animal"))).thenReturn(CommonResult.success(dictDatas));

        // 断言返回值
        assertEquals("狗", DictFrameworkUtils.parseDictDataLabel("animal", "dog"));
    }

    @Test
    public void testParseDictDataValue() {
        // mock 数据
        List<DictDataRespDTO> dictDatas = List.of(
                RandomUtils.randomPojo(DictDataRespDTO.class, o -> o.setDictType("animal").setValue("cat").setLabel("猫")),
                RandomUtils.randomPojo(DictDataRespDTO.class, o -> o.setDictType("animal").setValue("dog").setLabel("狗"))
        );

        // mock 方法
        when(dictDataApi.getDictDataList(eq("animal"))).thenReturn(CommonResult.success(dictDatas));

        // 断言返回值
        assertEquals("dog", DictFrameworkUtils.parseDictDataValue("animal", "狗"));
    }
}
