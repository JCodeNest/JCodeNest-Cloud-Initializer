package cn.jcodenest.framework.desensitize.core.slider.handler;

import cn.jcodenest.framework.desensitize.core.slider.annotation.CarLicenseDesensitize;

/**
 * {@link CarLicenseDesensitize} 的脱敏处理器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class CarLicenseDesensitization extends AbstractSliderDesensitizationHandler<CarLicenseDesensitize> {

    /**
     * 前缀保留长度
     *
     * @param annotation 注解信息
     * @return 前缀保留长度
     */
    @Override
    Integer getPrefixKeep(CarLicenseDesensitize annotation) {
        return annotation.prefixKeep();
    }

    /**
     * 后缀保留长度
     *
     * @param annotation 注解信息
     * @return 后缀保留长度
     */
    @Override
    Integer getSuffixKeep(CarLicenseDesensitize annotation) {
        return annotation.suffixKeep();
    }

    /**
     * 替换符
     *
     * @param annotation 注解信息
     * @return 替换符
     */
    @Override
    String getReplacer(CarLicenseDesensitize annotation) {
        return annotation.replacer();
    }

    /**
     * 是否禁用脱敏的 Spring EL 表达式, 如果返回 true 则跳过脱敏
     *
     * @param annotation 注解信息
     * @return 是否禁用脱敏的 Spring EL 表达式
     */
    @Override
    public String getDisable(CarLicenseDesensitize annotation) {
        return annotation.disable();
    }

}
