package cn.jcodenest.wiki.common.util.json.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 自定义基于时间戳的 LocalDateTime 反序列化器
 * <p>
 * 将毫秒级时间戳（Long 类型）反序列化为 LocalDateTime 对象。
 * 使用统一的时区配置（Asia/Shanghai）来确保时间转换的一致性。
 * <p>
 * 支持的输入格式：
 * <ul>
 *   <li>Long 类型的毫秒级时间戳</li>
 *   <li>String 类型的数字字符串（会尝试转换为 Long）</li>
 *   <li>null 值（返回 null）</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>前后端交互中接收时间戳格式的日期时间</li>
 *   <li>与第三方系统集成时接收时间戳格式</li>
 *   <li>从数据库读取时间戳格式的场景</li>
 * </ul>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimestampLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    /**
     * 默认时区：Asia/Shanghai
     * 使用固定时区而不是系统默认时区，确保在不同环境下的一致性
     */
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * 单例实例
     */
    public static final TimestampLocalDateTimeDeserializer INSTANCE = new TimestampLocalDateTimeDeserializer();

    /**
     * 将时间戳反序列化为 LocalDateTime 对象
     *
     * @param parser JSON 解析器
     * @param ctxt   反序列化上下文
     * @return LocalDateTime 对象，如果输入为 null 或无效则返回 null
     * @throws IOException 如果反序列化过程中发生 I/O 错误
     */
    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        JsonToken token = parser.getCurrentToken();

        // 处理 null 值
        if (token == JsonToken.VALUE_NULL) {
            return null;
        }

        try {
            long timestamp;

            // 处理数字类型的时间戳
            if (token == JsonToken.VALUE_NUMBER_INT) {
                timestamp = parser.getLongValue();
            }
            // 处理字符串类型的时间戳
            else if (token == JsonToken.VALUE_STRING) {
                String timestampStr = parser.getValueAsString();
                if (timestampStr == null || timestampStr.trim().isEmpty()) {
                    return null;
                }
                timestamp = Long.parseLong(timestampStr.trim());
            }
            // 其他类型尝试转换为 Long
            else {
                timestamp = parser.getValueAsLong();
            }

            // 验证时间戳的合理性（避免负数或过大的值）
            if (timestamp < 0) {
                return null;
            }

            // 将时间戳转换为 LocalDateTime
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE_ID);

        } catch (Exception e) {
            // 如果字符串无法转换为数字，返回 null
            return null;
        }
    }
}
