package cn.jcodenest.framework.desensitize.core.slider.annotation;

import cn.jcodenest.framework.desensitize.core.base.annotation.DesensitizeBy;
import cn.jcodenest.framework.desensitize.core.slider.handler.CarLicenseDesensitization;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.*;

/**
 * 车牌号
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = CarLicenseDesensitization.class)
public @interface CarLicenseDesensitize {

    /**
     * 前缀保留长度
     */
    int prefixKeep() default 3;

    /**
     * 后缀保留长度
     */
    int suffixKeep() default 1;

    /**
     * 车牌号替换规则
     * <p>
     * 比如：粤A66666 脱敏之后为粤A6***6
     */
    String replacer() default "*";

    /**
     * 是否禁用脱敏
     * <p>
     * 支持 Spring EL 表达式, 如果返回 true 则跳过脱敏
     */
    String disable() default "";
}
