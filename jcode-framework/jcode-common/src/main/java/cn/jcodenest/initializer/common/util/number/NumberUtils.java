package cn.jcodenest.initializer.common.util.number;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 数字工具类
 * 补全 {@link cn.hutool.core.util.NumberUtil}
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberUtils {

    /**
     * 将字符串转换为Long, 如果字符串为空则返回 null
     *
     * @param str 字符串
     * @return Long
     */
    public static Long parseLong(String str) {
        return CharSequenceUtil.isNotEmpty(str) ? Long.valueOf(str) : null;
    }

    /**
     * 将字符串转换为Integer, 如果字符串为空则返回 null
     *
     * @param str 字符串
     * @return Integer
     */
    public static Integer parseInt(String str) {
        return CharSequenceUtil.isNotEmpty(str) ? Integer.valueOf(str) : null;
    }

    /**
     * 判断字符串列表是否全部为数字
     *
     * @param values 字符串列表
     * @return true 全部为数字 ｜ false 不全部为数字
     */
    public static boolean isAllNumber(List<String> values) {
        if (CollUtil.isEmpty(values)) {
            return false;
        }

        for (String value : values) {
            if (!NumberUtil.isNumber(value)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 通过经纬度获取地球上两点之间的距离
     * <p>
     * 参考 <<a href="https://gitee.com/dromara/hutool/blob/1caabb586b1f95aec66a21d039c5695df5e0f4c1/hutool-core/src/main/java/cn/hutool/core/util/DistanceUtil.java">DistanceUtil</a>> 实现, 目前它已经被 hutool 删除
     *
     * @param lat1 经度1
     * @param lng1 纬度1
     * @param lat2 经度2
     * @param lng2 纬度2
     * @return 距离, 单位：千米
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = lat1 * Math.PI / 180.0;
        double radLat2 = lat2 * Math.PI / 180.0;
        double a = radLat1 - radLat2;
        double b = lng1 * Math.PI / 180.0 - lng2 * Math.PI / 180.0;
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        distance = distance * 6378.137;
        distance = Math.round(distance * 10000d) / 10000d;
        return distance;
    }

    /**
     * 提供精确的乘法运算
     * <p>
     * 和 hutool {@link NumberUtil#mul(BigDecimal...)} 的差别是, 如果存在 null, 则返回 null
     *
     * @param values 多个被乘值
     * @return 积
     */
    public static BigDecimal mul(BigDecimal... values) {
        for (BigDecimal value : values) {
            if (value == null) {
                return null;
            }
        }

        return NumberUtil.mul(values);
    }
}
