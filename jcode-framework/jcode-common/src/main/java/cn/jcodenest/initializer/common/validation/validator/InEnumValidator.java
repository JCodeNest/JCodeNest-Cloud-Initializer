package cn.jcodenest.initializer.common.validation.validator;

import cn.jcodenest.initializer.common.core.ArrayValuable;
import cn.jcodenest.initializer.common.validation.annotation.InEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 自定义范围校验
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class InEnumValidator implements ConstraintValidator<InEnum, Object> {

    /**
     * 枚举值
     */
    private List<?> values;

    /**
     * 校验
     *
     * @param o                          被校验的值
     * @param constraintValidatorContext 上下文
     * @return 是否校验通过
     */
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        // 为空时, 默认不校验, 即认为通过
        if (o == null) {
            return true;
        }

        // 校验通过
        if (values.contains(o)) {
            return true;
        }

        // 校验不通过, 自定义提示语句
        // 禁用默认的 message 的值
        constraintValidatorContext.disableDefaultConstraintViolation();
        // 重新添加错误提示语句
        constraintValidatorContext.buildConstraintViolationWithTemplate(
                constraintValidatorContext.getDefaultConstraintMessageTemplate()
                        .replace("\\{value}", values.toString())
        ).addConstraintViolation();
        return false;
    }

    /**
     * 初始化
     *
     * @param constraintAnnotation 注解
     */
    @Override
    public void initialize(InEnum constraintAnnotation) {
        ArrayValuable<?>[] inEnums = constraintAnnotation.value().getEnumConstants();
        if (inEnums.length == 0) {
            this.values = Collections.emptyList();
        } else {
            this.values = Arrays.asList(inEnums[0].array());
        }
    }
}
