package cn.jcodenest.framework.desensitize.core.slider.handler;

import cn.jcodenest.framework.desensitize.core.slider.annotation.FixedPhoneDesensitize;

/**
 * {@link FixedPhoneDesensitize} 的脱敏处理器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class FixedPhoneDesensitization extends AbstractSliderDesensitizationHandler<FixedPhoneDesensitize> {

    /**
     * 前缀保留长度
     *
     * @param annotation 注解信息
     * @return 前缀保留长度
     */
    @Override
    Integer getPrefixKeep(FixedPhoneDesensitize annotation) {
        return annotation.prefixKeep();
    }

    /**
     * 后缀保留长度
     *
     * @param annotation 注解信息
     * @return 后缀保留长度
     */
    @Override
    Integer getSuffixKeep(FixedPhoneDesensitize annotation) {
        return annotation.suffixKeep();
    }

    /**
     * 替换符
     *
     * @param annotation 注解信息
     * @return 替换符
     */
    @Override
    String getReplacer(FixedPhoneDesensitize annotation) {
        return annotation.replacer();
    }

}
