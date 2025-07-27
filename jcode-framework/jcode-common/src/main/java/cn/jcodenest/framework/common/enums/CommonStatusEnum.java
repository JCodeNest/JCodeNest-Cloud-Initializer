package cn.jcodenest.framework.common.enums;

import cn.hutool.core.util.ObjectUtil;
import cn.jcodenest.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 通用状态枚举
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
public enum CommonStatusEnum implements ArrayValuable<Integer> {

    ENABLE(0, "开启"),
    DISABLE(1, "关闭");

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 状态名
     */
    private final String name;

    /**
     * 数组
     */
    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(CommonStatusEnum::getStatus).toArray(Integer[]::new);

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
     * 是否开启
     *
     * @param status 状态
     * @return 是否开启
     */
    public static boolean isEnable(Integer status) {
        return ObjectUtil.equal(ENABLE.status, status);
    }

    /**
     * 是否关闭
     *
     * @param status 状态
     * @return 是否关闭
     */
    public static boolean isDisable(Integer status) {
        return ObjectUtil.equal(DISABLE.status, status);
    }
}
