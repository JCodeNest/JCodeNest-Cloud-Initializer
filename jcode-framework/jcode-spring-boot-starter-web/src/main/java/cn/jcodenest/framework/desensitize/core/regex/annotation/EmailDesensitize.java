package cn.jcodenest.framework.desensitize.core.regex.annotation;

import cn.jcodenest.framework.desensitize.core.base.annotation.DesensitizeBy;
import cn.jcodenest.framework.desensitize.core.regex.handler.EmailDesensitizationHandler;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.*;

/**
 * 邮箱脱敏注解
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
@DesensitizeBy(handler = EmailDesensitizationHandler.class)
public @interface EmailDesensitize {

    /**
     * 匹配的正则表达式
     */
    String regex() default "(^.)[^@]*(@.*$)";

    /**
     * 替换规则，邮箱;
     * <p>
     * 比如：example@gmail.com 脱敏之后为 e****@gmail.com
     */
    String replacer() default "$1****$2";

    /**
     * 是否禁用脱敏
     * <p>
     * 支持 Spring EL 表达式, 如果返回 true 则跳过脱敏
     */
    String disable() default "";
}
