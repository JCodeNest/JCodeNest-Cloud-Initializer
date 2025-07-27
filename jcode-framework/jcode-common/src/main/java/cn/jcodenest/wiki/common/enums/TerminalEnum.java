package cn.jcodenest.wiki.common.enums;

import cn.jcodenest.wiki.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 终端类型枚举
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Getter
@RequiredArgsConstructor
public enum TerminalEnum implements ArrayValuable<Integer> {

    UNKNOWN(0, "未知"),
    WECHAT_MINI_PROGRAM(10, "微信小程序"),
    WECHAT_WAP(11, "微信公众号"),
    H5(20, "H5 网页"),
    APP(31, "手机 App");

    /**
     * 终端码
     */
    private final Integer terminal;

    /**
     * 终端名称
     */
    private final String name;

    /**
     * 数组
     */
    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(TerminalEnum::getTerminal).toArray(Integer[]::new);

    /**
     * 转换为数组
     *
     * @return 数组
     */
    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
