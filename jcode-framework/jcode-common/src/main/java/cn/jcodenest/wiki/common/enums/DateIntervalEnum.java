package cn.jcodenest.wiki.common.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.jcodenest.wiki.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 时间间隔枚举
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Getter
@AllArgsConstructor
public enum DateIntervalEnum implements ArrayValuable<Integer> {

    DAY(1, "天"),
    WEEK(2, "周"),
    MONTH(3, "月"),
    QUARTER(4, "季度"),
    YEAR(5, "年");

    /**
     * 类型
     */
    private final Integer interval;

    /**
     * 名称
     */
    private final String name;

    /**
     * 数组
     */
    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(DateIntervalEnum::getInterval).toArray(Integer[]::new);

    /**
     * 转换为数组
     *
     * @return 数组
     */
    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    /**
     * 根据类型获取枚举
     *
     * @param interval 类型
     * @return 枚举
     */
    public static DateIntervalEnum valueOf(Integer interval) {
        return ArrayUtil.firstMatch(item -> item.getInterval().equals(interval), DateIntervalEnum.values());
    }
}
