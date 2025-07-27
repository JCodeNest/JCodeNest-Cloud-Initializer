package cn.jcodenest.wiki.common.util.date;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类 - 专注于Date类型的时间操作和Date与其他时间类型的转换
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    /**
     * 默认时区
     */
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * 秒转换成毫秒
     */
    public static final long SECOND_MILLIS = 1000;

    /**
     * 常用日期时间格式
     */
    public static final String FORMAT_YEAR_MONTH_DAY = "yyyy-MM-dd";
    public static final String FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";

    /**
     * 常用日期时间格式化器
     */
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_YEAR_MONTH_DAY);
    private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);

    /**
     * 将 LocalDateTime 转换为 Date
     *
     * @param localDateTime LocalDateTime对象
     * @return Date对象, 如果输入为null则返回null, 转换失败返回null
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }

        try {
            return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
        } catch (Exception e) {
            log.error("LocalDateTime转Date失败: {}", localDateTime, e);
            return null;
        }
    }

    /**
     * 将 Date 转换为 LocalDateTime
     *
     * @param date Date对象
     * @return LocalDateTime对象, 如果输入为null则返回null, 转换失败返回null
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }

        try {
            return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDateTime();
        } catch (Exception e) {
            log.error("Date转LocalDateTime失败: {}", date, e);
            return null;
        }
    }

    /**
     * 在当前时间基础上增加指定时长
     *
     * @param duration 时长
     * @return 增加时长后的Date对象, 如果duration为null则返回当前时间
     */
    public static Date addTime(Duration duration) {
        if (duration == null) {
            return new Date();
        }

        try {
            return new Date(System.currentTimeMillis() + duration.toMillis());
        } catch (Exception e) {
            log.error("时间增加失败: duration={}", duration, e);
            return new Date();
        }
    }

    /**
     * 判断指定时间是否已过期（是否在当前时间之前）
     *
     * @param time 要检查的时间
     * @return true-已过期, false-未过期或时间为null
     */
    public static boolean isExpired(LocalDateTime time) {
        if (time == null) {
            return false;
        }

        try {
            LocalDateTime now = LocalDateTime.now(DEFAULT_ZONE_ID);
            return now.isAfter(time);
        } catch (Exception e) {
            log.error("判断时间是否过期失败: time={}", time, e);
            return false;
        }
    }

    /**
     * 创建指定日期的Date对象（时间为00:00:00）
     *
     * @param year  年份
     * @param month 月份（1-12）
     * @param day   日期
     * @return 指定日期的Date对象, 创建失败返回null
     */
    public static Date buildTime(int year, int month, int day) {
        return buildTime(year, month, day, 0, 0, 0);
    }

    /**
     * 创建指定日期时间的Date对象
     *
     * @param year   年份
     * @param month  月份（1-12）
     * @param day    日期
     * @param hour   小时（0-23）
     * @param minute 分钟（0-59）
     * @param second 秒（0-59）
     * @return 指定日期时间的Date对象, 创建失败返回null
     */
    public static Date buildTime(int year, int month, int day, int hour, int minute, int second) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            // Calendar月份从0开始
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, second);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        } catch (Exception e) {
            log.error("创建指定时间失败: year={}, month={}, day={}, hour={}, minute={}, second={}", year, month, day, hour, minute, second, e);
            return null;
        }
    }

    /**
     * 获取两个Date中较大的一个
     *
     * @param a 第一个Date
     * @param b 第二个Date
     * @return 较大的Date, 如果其中一个为null则返回另一个, 都为null则返回null
     */
    public static Date max(Date a, Date b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.compareTo(b) > 0 ? a : b;
    }

    /**
     * 获取两个LocalDateTime中较大的一个
     *
     * @param a 第一个LocalDateTime
     * @param b 第二个LocalDateTime
     * @return 较大的LocalDateTime, 如果其中一个为null则返回另一个, 都为null则返回null
     */
    public static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.isAfter(b) ? a : b;
    }

    /**
     * 判断指定日期时间是否为今天
     *
     * @param date 要检查的日期时间
     * @return true-是今天, false-不是今天或日期为null
     */
    public static boolean isToday(LocalDateTime date) {
        if (date == null) {
            return false;
        }
        return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now(DEFAULT_ZONE_ID));
    }

    /**
     * 判断指定日期时间是否为昨天
     *
     * @param date 要检查的日期时间
     * @return true-是昨天, false-不是昨天或日期为null
     */
    public static boolean isYesterday(LocalDateTime date) {
        if (date == null) {
            return false;
        }
        return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now(DEFAULT_ZONE_ID).minusDays(1));
    }

    // ========== 时间戳相关 ==========

    /**
     * 获取当前时间戳（毫秒）
     *
     * @return 当前时间戳（毫秒）
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间戳（秒）
     *
     * @return 当前时间戳（秒）
     */
    public static long currentTimeSeconds() {
        return Instant.now().getEpochSecond();
    }

    /**
     * 时间戳转换为LocalDateTime
     *
     * @param timestamp 时间戳（毫秒）
     * @return LocalDateTime对象, 转换失败返回null
     */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE_ID);
        } catch (Exception e) {
            log.error("时间戳转LocalDateTime失败: {}", timestamp, e);
            return null;
        }
    }

    /**
     * LocalDateTime转换为时间戳
     *
     * @param localDateTime LocalDateTime对象
     * @return 时间戳（毫秒）, 转换失败返回0
     */
    public static long toTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return 0L;
        }

        try {
            return localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
        } catch (Exception e) {
            log.error("LocalDateTime转时间戳失败: {}", localDateTime, e);
            return 0L;
        }
    }

    // ========== 新增方法：Date格式化和解析 ==========

    /**
     * 格式化Date为字符串（默认格式：yyyy-MM-dd HH:mm:ss）
     *
     * @param date Date对象
     * @return 格式化后的字符串, 如果date为null或格式化失败则返回null
     */
    public static String format(Date date) {
        if (date == null) {
            return null;
        }

        try {
            LocalDateTime localDateTime = toLocalDateTime(date);
            return localDateTime != null ? localDateTime.format(DEFAULT_DATETIME_FORMATTER) : null;
        } catch (Exception e) {
            log.error("格式化Date失败: {}", date, e);
            return null;
        }
    }

    /**
     * 格式化Date为字符串（自定义格式）
     *
     * @param date    Date对象
     * @param pattern 格式模式
     * @return 格式化后的字符串, 如果参数为null或格式化失败则返回null
     */
    public static String format(Date date, String pattern) {
        if (date == null || StringUtils.isBlank(pattern)) {
            return null;
        }

        try {
            LocalDateTime localDateTime = toLocalDateTime(date);
            if (localDateTime == null) {
                return null;
            }
            return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("格式化Date失败: date={}, pattern={}", date, pattern, e);
            return null;
        }
    }

    /**
     * 解析日期时间字符串为Date（默认格式：yyyy-MM-dd HH:mm:ss）
     *
     * @param dateTimeStr 日期时间字符串
     * @return Date对象, 解析失败返回null
     */
    public static Date parseDate(String dateTimeStr) {
        if (StringUtils.isBlank(dateTimeStr)) {
            return null;
        }

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, DEFAULT_DATETIME_FORMATTER);
            return toDate(localDateTime);
        } catch (DateTimeParseException e) {
            log.error("解析日期时间字符串失败: {}", dateTimeStr, e);
            return null;
        }
    }

    /**
     * 解析日期时间字符串为Date（自定义格式）
     *
     * @param dateTimeStr 日期时间字符串
     * @param pattern     格式模式
     * @return Date对象, 解析失败返回null
     */
    public static Date parseDate(String dateTimeStr, String pattern) {
        if (StringUtils.isBlank(dateTimeStr) || StringUtils.isBlank(pattern)) {
            return null;
        }

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
            return toDate(localDateTime);
        } catch (DateTimeParseException e) {
            log.error("解析日期时间字符串失败: dateTimeStr={}, pattern={}", dateTimeStr, pattern, e);
            return null;
        }
    }
}
