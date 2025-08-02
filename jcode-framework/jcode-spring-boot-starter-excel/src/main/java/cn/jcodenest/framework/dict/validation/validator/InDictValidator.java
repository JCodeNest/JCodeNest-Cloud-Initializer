package cn.jcodenest.framework.dict.validation.validator;

import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.dict.core.util.DictFrameworkUtils;
import cn.jcodenest.framework.dict.validation.annotation.InDict;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

/**
 * 字典类型校验器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class InDictValidator implements ConstraintValidator<InDict, Object> {

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 初始化
     *
     * @param annotation 注解
     */
    @Override
    public void initialize(InDict annotation) {
        this.dictType = annotation.type();
    }

    /**
     * 是否通过
     *
     * @param value   字段值
     * @param context 上下文
     * @return 是否通过
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 为空时，默认不校验，即认为通过
        if (value == null) {
            return true;
        }

        // 校验通过
        final List<String> values = DictFrameworkUtils.getDictDataValueList(dictType);
        boolean match = values.stream().anyMatch(v -> StrUtil.equalsIgnoreCase(v, value.toString()));
        if (match) {
            return true;
        }

        // 校验不通过，自定义提示语句
        // 禁用默认的 message 的值，重新添加错误提示语句
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                context.getDefaultConstraintMessageTemplate().replace("\\{value}", values.toString())
        ).addConstraintViolation();
        return false;
    }
}
