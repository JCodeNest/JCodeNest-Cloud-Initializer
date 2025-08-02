package cn.jcodenest.framework.dict.validation.annotation;

import cn.jcodenest.framework.dict.validation.validator.InDictCollectionValidator;
import cn.jcodenest.framework.dict.validation.validator.InDictValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 字典验证注解
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
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
@Constraint(validatedBy = {InDictValidator.class, InDictCollectionValidator.class})
public @interface InDict {

    /**
     * 数据字典 type
     */
    String type();

    /**
     * 错误提示
     */
    String message() default "必须在指定范围 {value}";

    /**
     * 默认的组
     */
    Class<?>[] groups() default {};

    /**
     * 默认的负载
     */
    Class<? extends Payload>[] payload() default {};
}
