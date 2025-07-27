package cn.jcodenest.wiki.common.validation.validator;

import cn.hutool.core.collection.CollUtil;
import cn.jcodenest.wiki.common.core.ArrayValuable;
import cn.jcodenest.wiki.common.validation.annotation.InEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Collection;
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
public class InEnumCollectionValidator implements ConstraintValidator<InEnum, Collection<?>> {

    /**
     * 校验值
     */
    private List<?> values;

    /**
     * 校验方法
     *
     * @param objects                    校验值
     * @param constraintValidatorContext 上下文
     * @return 是否校验通过
     */
    @Override
    public boolean isValid(Collection<?> objects, ConstraintValidatorContext constraintValidatorContext) {
        // 允许为空
        if (objects == null) {
            return true;
        }

        // 校验通过
        if (CollUtil.containsAll(values, objects)) {
            return true;
        }

        // 校验不通过, 自定义提示语句
        // 禁用默认的 message 的值
        constraintValidatorContext.disableDefaultConstraintViolation();
        // 重新添加错误提示语句
        constraintValidatorContext.buildConstraintViolationWithTemplate(
                constraintValidatorContext.getDefaultConstraintMessageTemplate()
                        .replace("\\{value}", CollUtil.join(objects, ","))
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
