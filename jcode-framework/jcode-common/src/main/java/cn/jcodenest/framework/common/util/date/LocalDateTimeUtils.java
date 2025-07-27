package cn.jcodenest.framework.common.util.date;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.TemporalAccessorUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.jcodenest.framework.common.enums.DateIntervalEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.hutool.core.date.DatePattern.UTC_MS_WITH_XXX_OFFSET_PATTERN;
import static cn.hutool.core.date.DatePattern.createFormatter;

/**
 * LocalDateTime工具类 - 专注于LocalDateTime、LocalDate、LocalTime的操作
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
public class LocalDateTimeUtils {

    /**
     * 默认时区
     */
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * 空的 LocalDateTime 对象, 主要用于 DB 唯一索引的默认值
     */
    public static LocalDateTime EMPTY = buildTime(1970, 1, 1);

    /**
     * 常用日期时间格式化器
     */
    private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DEFAULT_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static DateTimeFormatter UTC_MS_WITH_XXX_OFFSET_FORMATTER = createFormatter(UTC_MS_WITH_XXX_OFFSET_PATTERN);

    // ========== 基础时间获取方法 ==========

    /**
     * 获取当前时间
     *
     * @return 当前LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(DEFAULT_ZONE_ID);
    }

    /**
     * 获取当前日期
     *
     * @return 当前LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now(DEFAULT_ZONE_ID);
    }

    /**
     * 解析时间字符串
     * <p>
     * 相比 {@link LocalDateTimeUtil#parse(CharSequence)} 方法来说, 会尽量去解析, 直到成功
     *
     * @param time 时间字符串
     * @return LocalDateTime对象, 解析失败返回null
     */
    public static LocalDateTime parse(String time) {
        if (StringUtils.isBlank(time)) {
            return null;
        }

        try {
            return LocalDateTimeUtil.parse(time, DatePattern.NORM_DATE_PATTERN);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTimeUtil.parse(time);
            } catch (DateTimeParseException ex) {
                log.error("解析时间字符串失败: {}", time, ex);
                return null;
            }
        }
    }

    /**
     * 在当前时间基础上增加指定时长
     *
     * @param duration 时长
     * @return 增加时长后的LocalDateTime, 如果duration为null则返回当前时间
     */
    public static LocalDateTime addTime(Duration duration) {
        if (duration == null) {
            return now();
        }

        return now().plus(duration);
    }

    /**
     * 在当前时间基础上减少指定时长
     *
     * @param duration 时长
     * @return 减少时长后的LocalDateTime, 如果duration为null则返回当前时间
     */
    public static LocalDateTime minusTime(Duration duration) {
        if (duration == null) {
            return now();
        }

        return now().minus(duration);
    }

    /**
     * 判断指定时间是否在当前时间之前
     *
     * @param date 要检查的时间
     * @return true-在当前时间之前, false-在当前时间之后或时间为null
     */
    public static boolean beforeNow(LocalDateTime date) {
        if (date == null) {
            return false;
        }

        return date.isBefore(now());
    }

    /**
     * 判断指定时间是否在当前时间之后
     *
     * @param date 要检查的时间
     * @return true-在当前时间之后, false-在当前时间之前或时间为null
     */
    public static boolean afterNow(LocalDateTime date) {
        if (date == null) {
            return false;
        }

        return date.isAfter(now());
    }

    /**
     * 创建指定日期的LocalDateTime对象（时间为00:00:00）
     *
     * @param year  年份
     * @param month 月份（1-12）
     * @param day   日期
     * @return 指定日期的LocalDateTime对象
     */
    public static LocalDateTime buildTime(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0, 0);
    }

    // ========== 格式化方法 ==========

    /**
     * 格式化日期时间（默认格式：yyyy-MM-dd HH:mm:ss）
     *
     * @param localDateTime LocalDateTime对象
     * @return 格式化后的字符串, 格式化失败返回null
     */
    public static String format(LocalDateTime localDateTime) {
        return format(localDateTime, DEFAULT_DATETIME_FORMATTER);
    }

    /**
     * 格式化日期（默认格式：yyyy-MM-dd）
     *
     * @param localDate LocalDate对象
     * @return 格式化后的字符串, 格式化失败返回null
     */
    public static String format(LocalDate localDate) {
        return format(localDate, DEFAULT_DATE_FORMATTER);
    }

    /**
     * 格式化时间（默认格式：HH:mm:ss）
     *
     * @param localTime LocalTime对象
     * @return 格式化后的字符串, 格式化失败返回null
     */
    public static String format(LocalTime localTime) {
        return format(localTime, DEFAULT_TIME_FORMATTER);
    }

    /**
     * 格式化日期时间（自定义格式）
     *
     * @param localDateTime LocalDateTime对象
     * @param pattern       格式模式
     * @return 格式化后的字符串, 格式化失败返回null
     */
    public static String format(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null || StringUtils.isBlank(pattern)) {
            return null;
        }

        try {
            return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("格式化LocalDateTime失败: localDateTime={}, pattern={}", localDateTime, pattern, e);
            return null;
        }
    }

    /**
     * 格式化日期时间（使用格式化器）
     *
     * @param localDateTime LocalDateTime对象
     * @param formatter     格式化器
     * @return 格式化后的字符串, 格式化失败返回null
     */
    public static String format(LocalDateTime localDateTime, DateTimeFormatter formatter) {
        if (localDateTime == null || formatter == null) {
            return null;
        }

        try {
            return localDateTime.format(formatter);
        } catch (Exception e) {
            log.error("格式化LocalDateTime失败: localDateTime={}, formatter={}", localDateTime, formatter, e);
            return null;
        }
    }

    /**
     * 格式化日期（使用格式化器）
     *
     * @param localDate LocalDate对象
     * @param formatter 格式化器
     * @return 格式化后的字符串, 格式化失败返回null
     */
    public static String format(LocalDate localDate, DateTimeFormatter formatter) {
        if (localDate == null || formatter == null) {
            return null;
        }

        try {
            return localDate.format(formatter);
        } catch (Exception e) {
            log.error("格式化LocalDate失败: localDate={}, formatter={}", localDate, formatter, e);
            return null;
        }
    }

    /**
     * 格式化时间（使用格式化器）
     *
     * @param localTime LocalTime对象
     * @param formatter 格式化器
     * @return 格式化后的字符串, 格式化失败返回null
     */
    public static String format(LocalTime localTime, DateTimeFormatter formatter) {
        if (localTime == null || formatter == null) {
            return null;
        }

        try {
            return localTime.format(formatter);
        } catch (Exception e) {
            log.error("格式化LocalTime失败: localTime={}, formatter={}", localTime, formatter, e);
            return null;
        }
    }

    // ========== 解析方法 ==========

    /**
     * 解析日期时间字符串（默认格式：yyyy-MM-dd HH:mm:ss）
     *
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime对象, 解析失败返回null
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr, DEFAULT_DATETIME_FORMATTER);
    }

    /**
     * 解析日期字符串（默认格式：yyyy-MM-dd）
     *
     * @param dateStr 日期字符串
     * @return LocalDate对象, 解析失败返回null
     */
    public static LocalDate parseDate(String dateStr) {
        return parseDate(dateStr, DEFAULT_DATE_FORMATTER);
    }

    /**
     * 解析时间字符串（默认格式：HH:mm:ss）
     *
     * @param timeStr 时间字符串
     * @return LocalTime对象, 解析失败返回null
     */
    public static LocalTime parseTime(String timeStr) {
        return parseTime(timeStr, DEFAULT_TIME_FORMATTER);
    }

    /**
     * 解析日期时间字符串（自定义格式）
     *
     * @param dateTimeStr 日期时间字符串
     * @param pattern     格式模式
     * @return LocalDateTime对象, 解析失败返回null
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (StringUtils.isBlank(dateTimeStr) || StringUtils.isBlank(pattern)) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            log.error("解析日期时间字符串失败: dateTimeStr={}, pattern={}", dateTimeStr, pattern, e);
            return null;
        }
    }

    /**
     * 解析日期时间字符串（使用格式化器）
     *
     * @param dateTimeStr 日期时间字符串
     * @param formatter   格式化器
     * @return LocalDateTime对象, 解析失败返回null
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, DateTimeFormatter formatter) {
        if (StringUtils.isBlank(dateTimeStr) || formatter == null) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            log.error("解析日期时间字符串失败: dateTimeStr={}, formatter={}", dateTimeStr, formatter, e);
            return null;
        }
    }

    /**
     * 解析日期字符串（使用格式化器）
     *
     * @param dateStr   日期字符串
     * @param formatter 格式化器
     * @return LocalDate对象, 解析失败返回null
     */
    public static LocalDate parseDate(String dateStr, DateTimeFormatter formatter) {
        if (StringUtils.isBlank(dateStr) || formatter == null) {
            return null;
        }

        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            log.error("解析日期字符串失败: dateStr={}, formatter={}", dateStr, formatter, e);
            return null;
        }
    }

    /**
     * 解析时间字符串（使用格式化器）
     *
     * @param timeStr   时间字符串
     * @param formatter 格式化器
     * @return LocalTime对象, 解析失败返回null
     */
    public static LocalTime parseTime(String timeStr, DateTimeFormatter formatter) {
        if (StringUtils.isBlank(timeStr) || formatter == null) {
            return null;
        }

        try {
            return LocalTime.parse(timeStr, formatter);
        } catch (DateTimeParseException e) {
            log.error("解析时间字符串失败: timeStr={}, formatter={}", timeStr, formatter, e);
            return null;
        }
    }

    // ========== 时间计算方法 ==========

    /**
     * 计算两个日期时间之间的天数差
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 天数差, 计算失败返回0
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0L;
        }

        try {
            return ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
        } catch (Exception e) {
            log.error("计算天数差失败: start={}, end={}", start, end, e);
            return 0L;
        }
    }

    /**
     * 构建时间范围
     *
     * @param year1  开始年份
     * @param month1 开始月份
     * @param day1   开始日期
     * @param year2  结束年份
     * @param month2 结束月份
     * @param day2   结束日期
     * @return 时间范围
     */
    public static LocalDateTime[] buildBetweenTime(int year1, int month1, int day1, int year2, int month2, int day2) {
        return new LocalDateTime[]{buildTime(year1, month1, day1), buildTime(year2, month2, day2)};
    }

    /**
     * 判指定断时间, 是否在该时间范围内
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param time      指定时间
     * @return 是否
     */
    public static boolean isBetween(LocalDateTime startTime, LocalDateTime endTime, String time) {
        if (startTime == null || endTime == null || time == null) {
            return false;
        }

        return LocalDateTimeUtil.isIn(Objects.requireNonNull(parse(time)), startTime, endTime);
    }

    /**
     * 判断当前时间是否在该时间范围内
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 是否
     */
    public static boolean isBetween(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return false;
        }

        return LocalDateTimeUtil.isIn(LocalDateTime.now(), startTime, endTime);
    }

    /**
     * 判断当前时间是否在该时间范围内
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 是否
     */
    public static boolean isBetween(String startTime, String endTime) {
        if (startTime == null || endTime == null) {
            return false;
        }

        LocalDate nowDate = LocalDate.now();
        return LocalDateTimeUtil.isIn(LocalDateTime.now(),
                LocalDateTime.of(nowDate, LocalTime.parse(startTime)),
                LocalDateTime.of(nowDate, LocalTime.parse(endTime)));
    }

    /**
     * 判断时间段是否重叠
     *
     * @param startTime1 开始 time1
     * @param endTime1   结束 time1
     * @param startTime2 开始 time2
     * @param endTime2   结束 time2
     * @return 重叠：true 不重叠：false
     */
    public static boolean isOverlap(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
        LocalDate nowDate = LocalDate.now();
        return LocalDateTimeUtil.isOverlap(
                LocalDateTime.of(nowDate, startTime1), LocalDateTime.of(nowDate, endTime1),
                LocalDateTime.of(nowDate, startTime2), LocalDateTime.of(nowDate, endTime2)
        );
    }

    /**
     * 获取指定日期所在的月份的开始时间
     * 例如：2023-09-30 00:00:00,000
     *
     * @param date 日期
     * @return 月份的开始时间
     */
    public static LocalDateTime beginOfMonth(LocalDateTime date) {
        return date.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
    }

    /**
     * 获取指定日期所在的月份的最后时间
     * 例如：2023-09-30 23:59:59,999
     *
     * @param date 日期
     * @return 月份的结束时间
     */
    public static LocalDateTime endOfMonth(LocalDateTime date) {
        return date.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
    }

    /**
     * 获得指定日期所在季度
     *
     * @param date 日期
     * @return 所在季度
     */
    public static int getQuarterOfYear(LocalDateTime date) {
        return (date.getMonthValue() - 1) / 3 + 1;
    }

    /**
     * 获取指定日期到现在过了几天, 如果指定日期在当前日期之后, 获取结果为负
     *
     * @param dateTime 日期
     * @return 相差天数
     */
    public static Long between(LocalDateTime dateTime) {
        return LocalDateTimeUtil.between(dateTime, LocalDateTime.now(), ChronoUnit.DAYS);
    }

    /**
     * 获取今天的开始时间
     *
     * @return 今天
     */
    public static LocalDateTime getToday() {
        return LocalDateTimeUtil.beginOfDay(LocalDateTime.now());
    }

    /**
     * 获取昨天的开始时间
     *
     * @return 昨天
     */
    public static LocalDateTime getYesterday() {
        return LocalDateTimeUtil.beginOfDay(LocalDateTime.now().minusDays(1));
    }

    /**
     * 获取本月的开始时间
     *
     * @return 本月
     */
    public static LocalDateTime getMonth() {
        return beginOfMonth(LocalDateTime.now());
    }

    /**
     * 获取本年的开始时间
     *
     * @return 本年
     */
    public static LocalDateTime getYear() {
        return LocalDateTime.now().with(TemporalAdjusters.firstDayOfYear()).with(LocalTime.MIN);
    }

    /**
     * 获取指定时间范围内的所有时间范围
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param interval  时间间隔
     * @return 时间范围列表
     */
    public static List<LocalDateTime[]> getDateRangeList(LocalDateTime startTime, LocalDateTime endTime, Integer interval) {
        // 1.1 找到枚举
        DateIntervalEnum intervalEnum = DateIntervalEnum.valueOf(interval);
        Assert.notNull(intervalEnum, "interval({}} 找不到对应的枚举", interval);

        // 1.2 将时间对齐
        startTime = LocalDateTimeUtil.beginOfDay(startTime);
        endTime = LocalDateTimeUtil.endOfDay(endTime);

        // 2. 循环, 生成时间范围
        List<LocalDateTime[]> timeRanges = new ArrayList<>();
        switch (intervalEnum) {
            case DAY:
                while (startTime.isBefore(endTime)) {
                    timeRanges.add(new LocalDateTime[]{startTime, startTime.plusDays(1).minusNanos(1)});
                    startTime = startTime.plusDays(1);
                }
                break;
            case WEEK:
                while (startTime.isBefore(endTime)) {
                    LocalDateTime endOfWeek = startTime.with(DayOfWeek.SUNDAY).plusDays(1).minusNanos(1);
                    timeRanges.add(new LocalDateTime[]{startTime, endOfWeek});
                    startTime = endOfWeek.plusNanos(1);
                }
                break;
            case MONTH:
                while (startTime.isBefore(endTime)) {
                    LocalDateTime endOfMonth = startTime.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).minusNanos(1);
                    timeRanges.add(new LocalDateTime[]{startTime, endOfMonth});
                    startTime = endOfMonth.plusNanos(1);
                }
                break;
            case QUARTER:
                while (startTime.isBefore(endTime)) {
                    int quarterOfYear = getQuarterOfYear(startTime);
                    LocalDateTime quarterEnd = quarterOfYear == 4
                            ? startTime.with(TemporalAdjusters.lastDayOfYear()).plusDays(1).minusNanos(1)
                            : startTime.withMonth(quarterOfYear * 3 + 1).withDayOfMonth(1).minusNanos(1);
                    timeRanges.add(new LocalDateTime[]{startTime, quarterEnd});
                    startTime = quarterEnd.plusNanos(1);
                }
                break;
            case YEAR:
                while (startTime.isBefore(endTime)) {
                    LocalDateTime endOfYear = startTime.with(TemporalAdjusters.lastDayOfYear()).plusDays(1).minusNanos(1);
                    timeRanges.add(new LocalDateTime[]{startTime, endOfYear});
                    startTime = endOfYear.plusNanos(1);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid interval: " + interval);
        }

        // 3. 兜底, 最后一个时间, 需要保持在 endTime 之前
        LocalDateTime[] lastTimeRange = CollUtil.getLast(timeRanges);
        if (lastTimeRange != null) {
            lastTimeRange[1] = endTime;
        }

        return timeRanges;
    }

    /**
     * 格式化时间范围
     *
     * @param startTime 开始时间
     * @param interval  时间间隔
     * @return 时间范围
     */
    public static String formatDateRange(LocalDateTime startTime, Integer interval) {
        // 1. 找到枚举
        DateIntervalEnum intervalEnum = DateIntervalEnum.valueOf(interval);
        Assert.notNull(intervalEnum, "interval({}} 找不到对应的枚举", interval);

        // 2. 循环, 生成时间范围
        return switch (intervalEnum) {
            case DAY -> LocalDateTimeUtil.format(startTime, DatePattern.NORM_DATE_PATTERN);
            case WEEK -> LocalDateTimeUtil.format(startTime, DatePattern.NORM_DATE_PATTERN)
                    + CharSequenceUtil.format("(第 {} 周)", LocalDateTimeUtil.weekOfYear(startTime));
            case MONTH -> LocalDateTimeUtil.format(startTime, DatePattern.NORM_MONTH_PATTERN);
            case QUARTER -> CharSequenceUtil.format("{}-Q{}", startTime.getYear(), getQuarterOfYear(startTime));
            case YEAR -> LocalDateTimeUtil.format(startTime, DatePattern.NORM_YEAR_PATTERN);
        };
    }

    /**
     * 将给定的 {@link LocalDateTime} 转换为自 Unix 纪元时间（1970-01-01T00:00:00Z）以来的秒数
     *
     * @param sourceDateTime 需要转换的本地日期时间, 不能为空
     * @return 自 1970-01-01T00:00:00Z 起的秒数（epoch second）
     * @throws NullPointerException 如果 {@code sourceDateTime} 为 {@code null}
     * @throws DateTimeException    如果转换过程中发生时间超出范围或其他时间处理异常
     */
    public static Long toEpochSecond(LocalDateTime sourceDateTime) {
        return TemporalAccessorUtil.toInstant(sourceDateTime).getEpochSecond();
    }
}
