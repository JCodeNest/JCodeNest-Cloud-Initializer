package cn.jcodenest.wiki.common.util.json.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import java.io.IOException;

/**
 * 自定义数字序列化器
 * <p>
 * 主要用于解决 JavaScript 中长整型精度丢失的问题。
 * JavaScript 中的 Number 类型采用 IEEE 754 双精度浮点数格式，
 * 只能安全表示 -(2^53-1) 到 2^53-1 之间的整数。
 * 超出此范围的长整型在序列化时会转换为字符串，避免精度丢失。
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@JacksonStdImpl
public class NumberSerializer extends com.fasterxml.jackson.databind.ser.std.NumberSerializer {

    /**
     * JavaScript 中 Number 类型能安全表示的最大整数值：2^53-1
     */
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;

    /**
     * JavaScript 中 Number 类型能安全表示的最小整数值：-(2^53-1)
     */
    private static final long MIN_SAFE_INTEGER = -9007199254740991L;

    /**
     * 单例实例，用于 Number 类型的序列化
     */
    public static final NumberSerializer INSTANCE = new NumberSerializer(Number.class);

    /**
     * 构造函数
     *
     * @param rawType 要序列化的数字类型
     */
    public NumberSerializer(Class<? extends Number> rawType) {
        super(rawType);
    }

    /**
     * 序列化数字值
     * <p>
     * 对于在 JavaScript 安全整数范围内的数字，直接序列化为数字类型；
     * 对于超出安全范围的长整型，序列化为字符串类型，避免精度丢失。
     *
     * @param value       要序列化的数字值
     * @param gen         JSON 生成器
     * @param serializers 序列化提供者
     * @throws IOException 如果序列化过程中发生 I/O 错误
     */
    @Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        long longValue = value.longValue();
        // 检查是否在 JavaScript 安全整数范围内
        if (longValue >= MIN_SAFE_INTEGER && longValue <= MAX_SAFE_INTEGER) {
            // 在安全范围内，直接序列化为数字
            super.serialize(value, gen, serializers);
        } else {
            // 超出安全范围，序列化为字符串避免精度丢失
            gen.writeString(value.toString());
        }
    }
}
