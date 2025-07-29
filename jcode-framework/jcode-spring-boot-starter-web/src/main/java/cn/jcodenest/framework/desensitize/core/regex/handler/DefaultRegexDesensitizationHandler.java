package cn.jcodenest.framework.desensitize.core.regex.handler;

import cn.jcodenest.framework.desensitize.core.regex.annotation.RegexDesensitize;

/**
 * {@link RegexDesensitize} 的正则脱敏处理器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class DefaultRegexDesensitizationHandler extends AbstractRegexDesensitizationHandler<RegexDesensitize> {

    /**
     * 获取注解上的 regex 参数
     *
     * @param annotation 注解信息
     * @return 正则表达式
     */
    @Override
    String getRegex(RegexDesensitize annotation) {
        return annotation.regex();
    }

    /**
     * 获取注解上的 replacer 参数
     *
     * @param annotation 注解信息
     * @return 待替换的字符串
     */
    @Override
    String getReplacer(RegexDesensitize annotation) {
        return annotation.replacer();
    }

    /**
     * 是否禁用脱敏的 Spring EL 表达式, 如果返回 true 则跳过脱敏
     *
     * @param annotation 注解信息
     * @return 是否禁用脱敏的 Spring EL 表达式
     */
    @Override
    public String getDisable(RegexDesensitize annotation) {
        return annotation.disable();
    }
}
