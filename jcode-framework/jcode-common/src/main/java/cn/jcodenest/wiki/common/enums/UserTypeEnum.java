package cn.jcodenest.wiki.common.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.jcodenest.wiki.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 全局用户类型枚举
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
public enum UserTypeEnum implements ArrayValuable<Integer> {

    /**
     * 面向 c 端, 普通用户
     */
    MEMBER(1, "会员"),

    /**
     * 面向 b 端, 管理后台
     */
    ADMIN(2, "管理员");

    /**
     * 类型
     */
    private final Integer value;

    /**
     * 类型名
     */
    private final String name;

    /**
     * 数组
     */
    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(UserTypeEnum::getValue).toArray(Integer[]::new);

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
     * 根据类型获得枚举
     *
     * @param value 类型
     * @return 枚举
     */
    public static UserTypeEnum valueOf(Integer value) {
        return ArrayUtil.firstMatch(userType -> userType.getValue().equals(value), UserTypeEnum.values());
    }
}
