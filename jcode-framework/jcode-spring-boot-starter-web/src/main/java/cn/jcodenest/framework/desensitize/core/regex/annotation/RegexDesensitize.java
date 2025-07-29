package cn.jcodenest.framework.desensitize.core.regex.annotation;

import cn.jcodenest.framework.desensitize.core.base.annotation.DesensitizeBy;
import cn.jcodenest.framework.desensitize.core.regex.handler.DefaultRegexDesensitizationHandler;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.*;

/**
 * 正则脱敏注解
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = DefaultRegexDesensitizationHandler.class)
public @interface RegexDesensitize {

    /**
     * 匹配的正则表达式（默认匹配所有）
     */
    String regex() default "^[\\s\\S]*$";

    /**
     * 替换规则, 会将匹配到的字符串全部替换成 replacer
     * <p>
     * 例如：regex=123; replacer=******
     * 原始字符串 123456789
     * 脱敏后字符串 ******456789
     */
    String replacer() default "******";

    /**
     * 是否禁用脱敏
     * <p>
     * 支持 Spring EL 表达式，如果返回 true 则跳过脱敏
     */
    String disable() default "";
}
