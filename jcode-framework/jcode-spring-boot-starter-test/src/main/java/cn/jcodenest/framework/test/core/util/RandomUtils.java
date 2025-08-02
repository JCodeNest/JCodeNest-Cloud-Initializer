package cn.jcodenest.framework.test.core.util;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.common.enums.CommonStatusEnum;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 随机工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class RandomUtils {

    /**
     * 随机字符串长度
     */
    private static final int RANDOM_STRING_LENGTH = 10;

    /**
     * tinyint 类型的最大值
     */
    private static final int TINYINT_MAX = 127;

    /**
     * 随机日期的最大天数
     */
    private static final int RANDOM_DATE_MAX = 30;

    /**
     * 随机集合的长度
     */
    private static final int RANDOM_COLLECTION_LENGTH = 5;

    /**
     * Podam 工厂
     */
    private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();

    // ========== Podam 配置 ==========
    static {
        // 字符串
        PODAM_FACTORY.getStrategy().addOrReplaceTypeManufacturer(String.class,
                (dataProviderStrategy, attributeMetadata, map) -> randomString());

        // Integer
        PODAM_FACTORY.getStrategy().addOrReplaceTypeManufacturer(Integer.class, (dataProviderStrategy, attributeMetadata, map) -> {
            // 如果是 status 的字段，返回 0 或 1
            if ("status".equals(attributeMetadata.getAttributeName())) {
                return RandomUtil.randomEle(CommonStatusEnum.values()).getStatus();
            }

            // 如果是 type、status 结尾的字段，返回 tinyint 范围
            if (StrUtil.endWithAnyIgnoreCase(attributeMetadata.getAttributeName(), "type", "status", "category", "scope", "result")) {
                return RandomUtil.randomInt(0, TINYINT_MAX + 1);
            }

            return RandomUtil.randomInt();
        });

        // LocalDateTime
        PODAM_FACTORY.getStrategy().addOrReplaceTypeManufacturer(LocalDateTime.class,
                (dataProviderStrategy, attributeMetadata, map) -> randomLocalDateTime());

        // Boolean
        PODAM_FACTORY.getStrategy().addOrReplaceTypeManufacturer(Boolean.class, (dataProviderStrategy, attributeMetadata, map) -> {
            // 如果是 deleted 的字段，返回非删除
            if ("deleted".equals(attributeMetadata.getAttributeName())) {
                return false;
            }

            return RandomUtil.randomBoolean();
        });
    }

    // ========== 随机方法 ==========

    /**
     * 随机字符串
     *
     * @return 随机字符串
     */
    public static String randomString() {
        return RandomUtil.randomString(RANDOM_STRING_LENGTH);
    }

    /**
     * 随机 Long Id
     *
     * @return 随机 Long Id
     */
    public static Long randomLongId() {
        return RandomUtil.randomLong(0, Long.MAX_VALUE);
    }

    /**
     * 随机 Integer
     *
     * @return 随机 Integer
     */
    public static Integer randomInteger() {
        return RandomUtil.randomInt(0, Integer.MAX_VALUE);
    }

    /**
     * 随机 Date
     *
     * @return 随机 Date
     */
    public static Date randomDate() {
        return RandomUtil.randomDay(0, RANDOM_DATE_MAX);
    }

    /**
     * 随机 LocalDateTime
     *
     * @return 随机 LocalDateTime
     */
    public static LocalDateTime randomLocalDateTime() {
        // 设置 Nano 为零的原因，避免 MySQL、H2 存储不到时间戳
        return LocalDateTimeUtil.of(randomDate()).withNano(0);
    }

    /**
     * 随机 Short
     *
     * @return 随机 Short
     */
    public static Short randomShort() {
        return (short) RandomUtil.randomInt(0, Short.MAX_VALUE);
    }

    /**
     * 随机 Set
     *
     * @param clazz 类
     * @param <T>   类型
     * @return 随机 Set
     */
    public static <T> Set<T> randomSet(Class<T> clazz) {
        return Stream.iterate(0, i -> i)
                .limit(RandomUtil.randomInt(1, RANDOM_COLLECTION_LENGTH))
                .map(i -> randomPojo(clazz))
                .collect(Collectors.toSet());
    }

    /**
     * 随机 CommonStatusEnum
     *
     * @return 随机 CommonStatusEnum
     */
    public static Integer randomCommonStatus() {
        return RandomUtil.randomEle(CommonStatusEnum.values()).getStatus();
    }

    /**
     * 随机 Email
     *
     * @return 随机 Email
     */
    public static String randomEmail() {
        return randomString() + "@qq.com";
    }

    /**
     * 随机 Mobile
     *
     * @return 随机 Mobile
     */
    public static String randomMobile() {
        return "13800138" + RandomUtil.randomNumbers(3);
    }

    /**
     * 随机 URL
     *
     * @return 随机 URL
     */
    public static String randomURL() {
        return "https://www.jcodenest.cn/" + randomString();
    }

    /**
     * 随机 Pojo
     *
     * @param clazz      类
     * @param consumers  消费者
     * @param <T>        类型
     * @return 随机 Pojo
     */
    @SafeVarargs
    public static <T> T randomPojo(Class<T> clazz, Consumer<T>... consumers) {
        T pojo = PODAM_FACTORY.manufacturePojo(clazz);
        // 非空时回调逻辑, 通过它可以实现 Pojo 的进一步处理
        if (ArrayUtil.isNotEmpty(consumers)) {
            Arrays.stream(consumers).forEach(consumer -> consumer.accept(pojo));
        }
        return pojo;
    }

    /**
     * 随机 Pojo
     *
     * @param clazz      类
     * @param type       类型
     * @param consumers  消费者
     * @param <T>        类型
     * @return 随机 Pojo
     */
    @SafeVarargs
    public static <T> T randomPojo(Class<T> clazz, Type type, Consumer<T>... consumers) {
        T pojo = PODAM_FACTORY.manufacturePojo(clazz, type);
        // 非空时回调逻辑, 通过它，可以实现 Pojo 的进一步处理
        if (ArrayUtil.isNotEmpty(consumers)) {
            Arrays.stream(consumers).forEach(consumer -> consumer.accept(pojo));
        }

        return pojo;
    }

    /**
     * 随机 Pojo List
     *
     * @param clazz      类
     * @param consumers  消费者
     * @param <T>        类型
     * @return 随机 Pojo List
     */
    @SafeVarargs
    public static <T> List<T> randomPojoList(Class<T> clazz, Consumer<T>... consumers) {
        int size = RandomUtil.randomInt(1, RANDOM_COLLECTION_LENGTH);
        return randomPojoList(clazz, size, consumers);
    }

    /**
     * 随机 Pojo List
     *
     * @param clazz      类
     * @param size       数量
     * @param consumers  消费者
     * @param <T>        类型
     * @return 随机 Pojo List
     */
    @SafeVarargs
    public static <T> List<T> randomPojoList(Class<T> clazz, int size, Consumer<T>... consumers) {
        return Stream.iterate(0, i -> i)
                .limit(size)
                .map(o -> randomPojo(clazz, consumers))
                .toList();
    }
}
