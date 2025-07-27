package cn.jcodenest.wiki.common.validation.annotation;

import cn.jcodenest.wiki.common.core.ArrayValuable;
import cn.jcodenest.wiki.common.validation.validator.InEnumCollectionValidator;
import cn.jcodenest.wiki.common.validation.validator.InEnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 范围限制自定义校验注解
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {InEnumValidator.class, InEnumCollectionValidator.class}
)
public @interface InEnum {

    /**
     * 获取实现 ArrayValuable 接口的类
     *
     * @return 实现 ArrayValuable 接口的类
     */
    Class<? extends ArrayValuable<?>> value();

    /**
     * 错误提示
     *
     * @return 错误提示
     */
    String message() default "必须在指定范围 {value}";

    /**
     * 验证组
     *
     * @return 验证组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     *
     * @return 负载
     */
    Class<? extends Payload>[] payload() default {};
}
