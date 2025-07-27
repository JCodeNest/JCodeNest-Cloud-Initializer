package cn.jcodenest.initializer.common.validation.validator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.jcodenest.initializer.common.validation.annotation.Telephone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 自定义电话格式校验
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class TelephoneValidator implements ConstraintValidator<Telephone, String> {

    /**
     * 初始化
     *
     * @param constraintAnnotation 注解
     */
    @Override
    public void initialize(Telephone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * 校验
     *
     * @param s                          字符串
     * @param constraintValidatorContext 上下文
     * @return true-校验通过, false-校验不通过
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        // 如果手机号为空, 默认不校验, 即校验通过
        if (CharSequenceUtil.isEmpty(s)) {
            return true;
        }

        // 校验手机
        return PhoneUtil.isTel(s) || PhoneUtil.isPhone(s);
    }
}
