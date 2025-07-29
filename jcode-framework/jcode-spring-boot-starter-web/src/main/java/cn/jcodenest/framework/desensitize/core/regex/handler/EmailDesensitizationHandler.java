package cn.jcodenest.framework.desensitize.core.regex.handler;

import cn.jcodenest.framework.desensitize.core.regex.annotation.EmailDesensitize;

/**
 * {@link EmailDesensitize} 的脱敏处理器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class EmailDesensitizationHandler extends AbstractRegexDesensitizationHandler<EmailDesensitize> {

    /**
     * 获取注解上的 regex 参数
     *
     * @param annotation 注解信息
     * @return 正则表达式
     */
    @Override
    String getRegex(EmailDesensitize annotation) {
        return annotation.regex();
    }

    /**
     * 获取注解上的 replacer 参数
     *
     * @param annotation 注解信息
     * @return 待替换的字符串
     */
    @Override
    String getReplacer(EmailDesensitize annotation) {
        return annotation.replacer();
    }
}
