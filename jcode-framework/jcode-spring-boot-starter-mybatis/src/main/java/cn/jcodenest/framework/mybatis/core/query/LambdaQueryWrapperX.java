package cn.jcodenest.framework.mybatis.core.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * 扩展 MyBatis-Plus 的 LambdaQueryWrapper 类，提供条件拼接的增强方法。
 *
 * <p>
 * 支持 xxxIfPresent 方法，仅在值非空时拼接条件，避免无效条件拼接。
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class LambdaQueryWrapperX<T> extends LambdaQueryWrapper<T> {

    /**
     * 如果值非空，添加 LIKE 条件。
     *
     * @param column 字段
     * @param val    值
     * @return 当前查询包装器
     */
    public LambdaQueryWrapperX<T> likeIfPresent(SFunction<T, ?> column, String val) {
        Objects.requireNonNull(column, "Column must not be null");
        if (StringUtils.hasText(val)) {
            super.like(column, val);
        }
        return this;
    }

    /**
     * 如果集合非空，添加 IN 条件。
     *
     * @param column 字段
     * @param values 值集合
     * @return 当前查询包装器
     */
    public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
        Objects.requireNonNull(column, "Column must not be null");
        if (values != null && !values.isEmpty()) {
            super.in(column, values);
        }
        return this;
    }

    /**
     * 如果数组非空，添加 IN 条件。
     *
     * @param column 字段
     * @param values 值数组
     * @return 当前查询包装器
     */
    public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Object... values) {
        Objects.requireNonNull(column, "Column must not be null");
        if (values != null && values.length > 0) {
            super.in(column, values);
        }
        return this;
    }

    /**
     * 如果值非空，添加等于 (EQ) 条件。
     *
     * @param column 字段
     * @param val    值
     * @return 当前查询包装器
     */
    public LambdaQueryWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object val) {
        Objects.requireNonNull(column, "Column must not be null");
        if (val != null) {
            super.eq(column, val);
        }
        return this;
    }

    /**
     * 如果值非空，添加不等于 (NE) 条件。
     *
     * @param column 字段
     * @param val    值
     * @return 当前查询包装器
     */
    public LambdaQueryWrapperX<T> neIfPresent(SFunction<T, ?> column, Object val) {
        Objects.requireNonNull(column, "Column must not be null");
        if (val != null) {
            super.ne(column, val);
        }
        return this;
    }

    /**
     * 如果值非空，添加大于 (GT) 条件。
     *
     * @param column 字段
     * @param val    值
     * @return 当前查询包装器
     */
    public LambdaQueryWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object val) {
        Objects.requireNonNull(column, "Column must not be null");
        if (val != null) {
            super.gt(column, val);
        }
        return this;
    }

    /**
     * 如果值非空，添加大于等于 (GE) 条件。
     *
     * @param column 字段
     * @param val    值
     * @return 当前查询包装器
     */
    public LambdaQueryWrapperX<T> geIfPresent(SFunction<T, ?> column, Object val) {
        Objects.requireNonNull(column, "Column must not be null");
        if (val != null) {
            super.ge(column, val);
        }
        return this;
    }

    /**
     * 如果值非空，添加小于 (LT) 条件。
     *
     * @param column 字段
     * @param val    值
     * @return 当前查询包装器
     */
    public LambdaQueryWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object val) {
        Objects.requireNonNull(column, "Column must not be null");
        if (val != null) {
            super.lt(column, val);
        }
        return this;
    }

    /**
     * 如果值非空，添加小于等于 (LE) 条件。
     *
     * @param column 字段
     * @param val    值
     * @return 当前查询包装器
     */
    public LambdaQueryWrapperX<T> leIfPresent(SFunction<T, ?> column, Object val) {
        Objects.requireNonNull(column, "Column must not be null");
        if (val != null) {
            super.le(column, val);
        }
        return this;
    }

    /**
     * 如果值非空，添加 BETWEEN 条件。
     *
     * @param column 字段
     * @param val1   起始值
     * @param val2   结束值
     * @return 当前查询包装器
     */
    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
        Objects.requireNonNull(column, "Column must not be null");
        if (val1 != null && val2 != null) {
            super.between(column, val1, val2);
        } else if (val1 != null) {
            super.ge(column, val1);
        } else if (val2 != null) {
            super.le(column, val2);
        }
        return this;
    }

    /**
     * 如果数组非空，添加 BETWEEN 条件。
     *
     * @param column 字段
     * @param values 值数组，包含起始值和结束值
     * @return 当前查询包装器
     */
    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object[] values) {
        Objects.requireNonNull(column, "Column must not be null");
        Object val1 = values != null && values.length > 0 ? values[0] : null;
        Object val2 = values != null && values.length > 1 ? values[1] : null;
        return betweenIfPresent(column, val1, val2);
    }

    // ========== 重写父类方法，支持链式调用 ==========

    /**
     * 添加等于 (EQ) 条件，支持条件控制。
     *
     * @param condition 条件是否生效
     * @param column    字段
     * @param val       值
     * @return 当前查询包装器
     */
    @Override
    public LambdaQueryWrapperX<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        super.eq(condition, column, val);
        return this;
    }

    /**
     * 添加等于 (EQ) 条件。
     *
     * @param column 字段
     * @param val    值
     * @return 当前查询包装器
     */
    @Override
    public LambdaQueryWrapperX<T> eq(SFunction<T, ?> column, Object val) {
        super.eq(column, val);
        return this;
    }

    /**
     * 添加降序排序。
     *
     * @param column 字段
     * @return 当前查询包装器
     */
    @Override
    public LambdaQueryWrapperX<T> orderByDesc(SFunction<T, ?> column) {
        super.orderByDesc(true, column);
        return this;
    }

    /**
     * 添加自定义 SQL 片段。
     *
     * @param lastSql 自定义 SQL
     * @return 当前查询包装器
     */
    @Override
    public LambdaQueryWrapperX<T> last(String lastSql) {
        super.last(lastSql);
        return this;
    }

    /**
     * 添加 IN 条件。
     *
     * @param column 字段
     * @param coll   值集合
     * @return 当前查询包装器
     */
    @Override
    public LambdaQueryWrapperX<T> in(SFunction<T, ?> column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }
}
