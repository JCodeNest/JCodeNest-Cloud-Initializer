package cn.jcodenest.framework.common.util.number;

import cn.hutool.core.math.Money;
import cn.hutool.core.util.NumberUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoneyUtils {

    /**
     * 金额的小数位数, 两位小数
     */
    private static final int PRICE_SCALE = 2;

    /**
     * 百分比对应的 BigDecimal 对象
     */
    public static final BigDecimal PERCENT_100 = BigDecimal.valueOf(100);

    /**
     * 计算百分比金额, 四舍五入
     *
     * @param price 金额
     * @param rate  百分比, 例如 56.77% 则传入 56.77
     * @return 百分比金额
     */
    public static Integer calculateRatePrice(Integer price, Double rate) {
        return calculateRatePrice(price, rate, 0, RoundingMode.HALF_UP).intValue();
    }

    /**
     * 计算百分比金额, 向下传入
     *
     * @param price 金额
     * @param rate  百分比, 例如 56.77% 则传入 56.77
     * @return 百分比金额
     */
    public static Integer calculateRatePriceFloor(Integer price, Double rate) {
        return calculateRatePrice(price, rate, 0, RoundingMode.FLOOR).intValue();
    }

    /**
     * 计算百分比金额
     *
     * @param price   金额（单位分）
     * @param count   数量
     * @param percent 折扣（单位分）, 列如 60.2%, 则传入 6020
     * @return 商品总价
     */
    public static Integer calculator(Integer price, Integer count, Integer percent) {
        price = price * count;
        if (percent == null) {
            return price;
        }

        return MoneyUtils.calculateRatePriceFloor(price, (double) percent / 100);
    }

    /**
     * 计算百分比金额
     *
     * @param price        金额
     * @param rate         百分比, 例如 56.77% 则传入 56.77
     * @param scale        保留小数位数
     * @param roundingMode 舍入模式
     */
    public static BigDecimal calculateRatePrice(Number price, Number rate, int scale, RoundingMode roundingMode) {
        return NumberUtil.toBigDecimal(price)
                .multiply(NumberUtil.toBigDecimal(rate))
                .divide(BigDecimal.valueOf(100), scale, roundingMode);
    }

    /**
     * 分转元
     *
     * @param fen 分
     * @return 元
     */
    public static BigDecimal fenToYuan(int fen) {
        return new Money(0, fen).getAmount();
    }

    /**
     * 分转元（字符串）
     * <p>
     * 例如 fen 为 1 时, 则结果为 0.01
     *
     * @param fen 分
     * @return 元
     */
    public static String fenToYuanStr(int fen) {
        return new Money(0, fen).toString();
    }

    /**
     * 金额相乘, 默认进行四舍五入
     * <p>
     * 位数：{@link #PRICE_SCALE}
     *
     * @param price 金额
     * @param count 数量
     * @return 金额相乘结果
     */
    public static BigDecimal priceMultiply(BigDecimal price, BigDecimal count) {
        if (price == null || count == null) {
            return null;
        }

        return price.multiply(count).setScale(PRICE_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 金额相乘（百分比）, 默认进行四舍五入
     * <p>
     * 位数：{@link #PRICE_SCALE}
     *
     * @param price   金额
     * @param percent 百分比
     * @return 金额相乘结果
     */
    public static BigDecimal priceMultiplyPercent(BigDecimal price, BigDecimal percent) {
        if (price == null || percent == null) {
            return null;
        }

        return price.multiply(percent).divide(PERCENT_100, PRICE_SCALE, RoundingMode.HALF_UP);
    }
}
