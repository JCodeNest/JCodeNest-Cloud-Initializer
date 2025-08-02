package cn.jcodenest.framework.dict.validation.validator;

import cn.hutool.core.collection.CollUtil;
import cn.jcodenest.framework.dict.core.util.DictFrameworkUtils;
import cn.jcodenest.framework.dict.validation.annotation.InDict;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.List;

/**
 * 1
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class InDictCollectionValidator implements ConstraintValidator<InDict, Collection<?>> {

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
     * 是否有效
     *
     * @param list    集合
     * @param context 上下文
     * @return 是否有效
     */
    @Override
    public boolean isValid(Collection<?> list, ConstraintValidatorContext context) {
        // 为空时，默认不校验，即认为通过
        if (CollUtil.isEmpty(list)) {
            return true;
        }

        // 校验全部通过
        List<String> dbValues = DictFrameworkUtils.getDictDataValueList(dictType);
        boolean match = list.stream().allMatch(v -> dbValues.stream().anyMatch(dbValue -> dbValue.equalsIgnoreCase(v.toString())));
        if (match) {
            return true;
        }

        // 校验不通过，自定义提示语句
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                context.getDefaultConstraintMessageTemplate().replace("\\{value}", dbValues.toString())
        ).addConstraintViolation();
        return false;
    }
}
