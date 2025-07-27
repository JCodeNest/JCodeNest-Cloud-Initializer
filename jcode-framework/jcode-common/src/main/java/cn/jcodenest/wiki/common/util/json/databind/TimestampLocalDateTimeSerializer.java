package cn.jcodenest.wiki.common.util.json.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 自定义基于时间戳的 LocalDateTime 序列化器
 * <p>
 * 将 LocalDateTime 对象序列化为毫秒级时间戳（Long 类型）。
 * 使用统一的时区配置（Asia/Shanghai）来确保时间转换的一致性。
 * <p>
 * 使用场景：
 * <ul>
 *   <li>前后端交互中需要传递时间戳格式的日期时间</li>
 *   <li>与第三方系统集成时需要时间戳格式</li>
 *   <li>数据库存储时间戳格式的场景</li>
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
public class TimestampLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    /**
     * 默认时区：Asia/Shanghai
     * 使用固定时区而不是系统默认时区，确保在不同环境下的一致性
     */
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * 单例实例
     */
    public static final TimestampLocalDateTimeSerializer INSTANCE = new TimestampLocalDateTimeSerializer();

    /**
     * 将 LocalDateTime 对象序列化为毫秒级时间戳
     *
     * @param value       要序列化的 LocalDateTime 对象
     * @param gen         JSON 生成器
     * @param serializers 序列化提供者
     * @throws IOException 如果序列化过程中发生 I/O 错误
     */
    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        try {
            // 将 LocalDateTime 转换为指定时区的时间戳（毫秒）
            long timestamp = value.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
            gen.writeNumber(timestamp);
        } catch (Exception e) {
            // 如果转换失败，写入 null 值
            gen.writeNull();
        }
    }
}
