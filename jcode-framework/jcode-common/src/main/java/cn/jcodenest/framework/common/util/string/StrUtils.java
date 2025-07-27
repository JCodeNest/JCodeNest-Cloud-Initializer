package cn.jcodenest.framework.common.util.string;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.aspectj.lang.JoinPoint;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 字符串工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StrUtils {

    /**
     * 限制字符串的最大长度
     *
     * @param str       字符串
     * @param maxLength 最大长度
     * @return 限制后的字符串
     */
    public static String maxLength(CharSequence str, int maxLength) {
        return CharSequenceUtil.maxLength(str, maxLength - 3);
    }

    /**
     * 给定字符串是否以任何一个字符串开始
     * 给定字符串和数组为空都返回 false
     *
     * @param str      给定字符串
     * @param prefixes 需要检测的开始字符串
     * @since 3.0.6
     */
    public static boolean startWithAny(String str, Collection<String> prefixes) {
        if (CharSequenceUtil.isEmpty(str) || ArrayUtil.isEmpty(prefixes)) {
            return false;
        }

        for (CharSequence suffix : prefixes) {
            if (CharSequenceUtil.startWith(str, suffix, false)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 将字符串切分为long列表
     *
     * @param value     字符串
     * @param separator 分隔符
     * @return long列表
     */
    public static List<Long> splitToLong(String value, CharSequence separator) {
        long[] longs = CharSequenceUtil.splitToLong(value, separator);
        return Arrays.stream(longs).boxed().toList();
    }
    
    /**
     * 将字符串切分为long列表
     *
     * @param value 字符串
     * @return long列表
     */
    public static Set<Long> splitToLongSet(String value) {
        return splitToLongSet(value, StrPool.COMMA);
    }

    /**
     * 将字符串切分为long列表
     *
     * @param value     字符串
     * @param separator 分隔符
     * @return long列表
     */
    public static Set<Long> splitToLongSet(String value, CharSequence separator) {
        long[] longs = CharSequenceUtil.splitToLong(value, separator);
        return Arrays.stream(longs).boxed().collect(Collectors.toSet());
    }

    /**
     * 将字符串切分为int列表
     *
     * @param value     字符串
     * @param separator 分隔符
     * @return int列表
     */
    public static List<Integer> splitToInteger(String value, CharSequence separator) {
        int[] integers = CharSequenceUtil.splitToInt(value, separator);
        return Arrays.stream(integers).boxed().toList();
    }

    /**
     * 移除字符串中, 包含指定字符串的行
     *
     * @param content  字符串
     * @param sequence 包含的字符串
     * @return 移除后的字符串
     */
    public static String removeLineContains(String content, String sequence) {
        if (CharSequenceUtil.isEmpty(content) || CharSequenceUtil.isEmpty(sequence)) {
            return content;
        }
        
        return Arrays.stream(content.split("\n"))
                .filter(line -> !line.contains(sequence))
                .collect(Collectors.joining("\n"));
    }

    /**
     * 拼接方法的参数
     * <p>
     * 特殊：排除一些无法序列化的参数, 如 ServletRequest、ServletResponse、MultipartFile
     *
     * @param joinPoint 连接点
     * @return 拼接后的参数
     */
    public static String joinMethodArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (ArrayUtil.isEmpty(args)) {
            return "";
        }

        return ArrayUtil.join(args, ",", item -> {
            if (item == null) {
                return "";
            }

            // 讨论可见：https://t.zsxq.com/XUJVk、https://t.zsxq.com/MnKcL
            String clazzName = item.getClass().getName();
            if (CharSequenceUtil.startWithAny(clazzName, "javax.servlet", "jakarta.servlet", "org.springframework.web")) {
                return "";
            }

            return item;
        });
    }
}
