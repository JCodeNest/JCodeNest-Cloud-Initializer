package cn.jcodenest.framework.desensitize.core.slider.handler;

import cn.jcodenest.framework.desensitize.core.slider.annotation.PasswordDesensitize;

/**
 * {@link PasswordDesensitize} 的脱敏处理器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class PasswordDesensitization extends AbstractSliderDesensitizationHandler<PasswordDesensitize> {

    /**
     * 前缀保留长度
     *
     * @param annotation 注解信息
     * @return 前缀保留长度
     */
    @Override
    Integer getPrefixKeep(PasswordDesensitize annotation) {
        return annotation.prefixKeep();
    }

    /**
     * 后缀保留长度
     *
     * @param annotation 注解信息
     * @return 后缀保留长度
     */
    @Override
    Integer getSuffixKeep(PasswordDesensitize annotation) {
        return annotation.suffixKeep();
    }

    /**
     * 替换符
     *
     * @param annotation 注解信息
     * @return 替换符
     */
    @Override
    String getReplacer(PasswordDesensitize annotation) {
        return annotation.replacer();
    }
}
