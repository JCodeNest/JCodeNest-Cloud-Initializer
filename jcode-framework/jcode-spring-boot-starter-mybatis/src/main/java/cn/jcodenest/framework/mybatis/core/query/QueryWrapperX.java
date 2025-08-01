package cn.jcodenest.framework.mybatis.core.query;

import cn.jcodenest.framework.mybatis.core.util.JdbcUtils;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * 扩展 MyBatis-Plus 的 QueryWrapper 类，提供条件拼接的增强方法。
 *
 * <p>
 * 支持 xxxIfPresent 方法，仅在值非空时拼接条件，避免无效条件拼接。
 * </p>
 *
 * @param <T> 数据类型
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class QueryWrapperX<T> extends QueryWrapper<T> {

    /**
     * 如果值非空，添加 LIKE 条件。
     *
     * @param column 字段
     * @param val    值
     * @return 当前查询包装器
     */
    public QueryWrapperX<T> likeIfPresent(String column, String val) {
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
    public QueryWrapperX<T> inIfPresent(String column, Collection<?> values) {
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
    public QueryWrapperX<T> inIfPresent(String column, Object... values) {
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
    public QueryWrapperX<T> eqIfPresent(String column, Object val) {
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
    public QueryWrapperX<T> neIfPresent(String column, Object val) {
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
    public QueryWrapperX<T> gtIfPresent(String column, Object val) {
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
    public QueryWrapperX<T> geIfPresent(String column, Object val) {
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
    public QueryWrapperX<T> ltIfPresent(String column, Object val) {
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
    public QueryWrapperX<T> leIfPresent(String column, Object val) {
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
    public QueryWrapperX<T> betweenIfPresent(String column, Object val1, Object val2) {
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
    public QueryWrapperX<T> betweenIfPresent(String column, Object[] values) {
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
    public QueryWrapperX<T> eq(boolean condition, String column, Object val) {
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
    public QueryWrapperX<T> eq(String column, Object val) {
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
    public QueryWrapperX<T> orderByDesc(String column) {
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
    public QueryWrapperX<T> last(String lastSql) {
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
    public QueryWrapperX<T> in(String column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }

    /**
     * 限制查询返回前 N 条记录。
     * <p>
     * 根据数据库类型生成对应的 SQL 限制语法（如 LIMIT、TOP、ROWNUM）。
     * 注意：多数据源环境下，语法差异可能导致兼容性问题。
     * </p>
     *
     * @param n 限制的记录数
     * @return 当前查询包装器
     * @throws IllegalArgumentException 如果 n 小于或等于 0
     */
    public QueryWrapperX<T> limitN(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }

        DbType dbType = JdbcUtils.getDbType();
        switch (dbType) {
            case ORACLE, ORACLE_12C -> super.le("ROWNUM", n);
            case SQL_SERVER, SQL_SERVER2005 -> super.select("TOP " + n + " *");
            default -> super.last("LIMIT " + n);
        }

        return this;
    }
}
