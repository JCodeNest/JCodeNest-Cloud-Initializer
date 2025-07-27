package cn.jcodenest.initializer.common.validation.validator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.jcodenest.initializer.common.util.validation.ValidationUtils;
import cn.jcodenest.initializer.common.validation.annotation.Mobile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 自定义手机号校验
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class MobileValidator implements ConstraintValidator<Mobile, String> {

    /**
     * 校验手机号
     *
     * @param s                          手机号
     * @param constraintValidatorContext 校验器上下文
     * @return 校验结果
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        // 如果手机号为空, 默认不校验, 即校验通过
        if (CharSequenceUtil.isEmpty(s)) {
            return true;
        }

        // 校验手机
        return ValidationUtils.isMobile(s);
    }

    /**
     * 初始化
     *
     * @param constraintAnnotation 注解
     */
    @Override
    public void initialize(Mobile constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
