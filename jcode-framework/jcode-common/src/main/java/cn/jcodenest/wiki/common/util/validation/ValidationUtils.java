package cn.jcodenest.wiki.common.util.validation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * 校验工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtils {

    /**
     * 手机号正则表达式
     */
    private static final Pattern PATTERN_MOBILE = Pattern.compile("^(?:(?:\\+|00)86)?1(?:(?:3[\\d])|(?:4[0,1,4-9])|(?:5[0-3,5-9])|(?:6[2,5-7])|(?:7[0-8])|(?:8[\\d])|(?:9[0-3,5-9]))\\d{8}$");

    /**
     * URL正则表达式
     */
    private static final Pattern PATTERN_URL = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    /**
     * XML NCName正则表达式
     */
    private static final Pattern PATTERN_XML_NCNAME = Pattern.compile("[a-zA-Z_][\\-_.0-9_a-zA-Z$]*");

    /**
     * 校验手机号
     *
     * @param mobile 手机号
     * @return true-是手机号, false-不是手机号
     */
    public static boolean isMobile(String mobile) {
        return StringUtils.hasText(mobile) && PATTERN_MOBILE.matcher(mobile).matches();
    }

    /**
     * 校验URL
     *
     * @param url URL
     * @return true-是URL, false-不是URL
     */
    public static boolean isURL(String url) {
        return StringUtils.hasText(url) && PATTERN_URL.matcher(url).matches();
    }

    /**
     * 校验XML NCName
     *
     * @param str 字符串
     * @return true-是XML NCName, false-不是XML NCName
     */
    public static boolean isXmlNCName(String str) {
        return StringUtils.hasText(str) && PATTERN_XML_NCNAME.matcher(str).matches();
    }

    /**
     * 校验对象
     *
     * @param object 待校验对象
     * @param groups 待校验的组
     */
    public static void validate(Object object, Class<?>... groups) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Assert.notNull(validator);
        validate(validator, object, groups);
    }

    /**
     * 校验对象
     *
     * @param validator 校验器
     * @param object    待校验对象
     * @param groups    待校验的组
     */
    public static void validate(Validator validator, Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (CollUtil.isNotEmpty(constraintViolations)) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}
