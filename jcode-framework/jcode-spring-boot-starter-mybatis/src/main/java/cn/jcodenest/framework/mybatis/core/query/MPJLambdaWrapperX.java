package cn.jcodenest.framework.mybatis.core.query;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 扩展 MyBatis-Plus Join 的 MPJLambdaWrapper 类，提供条件拼接和连表查询的增强方法。
 *
 * <p>
 * 1. 支持 xxxIfPresent 方法，仅在值非空时拼接条件，避免无效条件拼接。
 * 2. 支持任意表字段的 SFunction<S, ?>，通过泛型 S 自动推断类型。
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
public class MPJLambdaWrapperX<T> extends MPJLambdaWrapper<T> {

    /**
     * 如果值非空，添加 LIKE 条件。
     *
     * @param column 字段
     * @param val    值
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    public <S> MPJLambdaWrapperX<T> likeIfPresent(SFunction<S, ?> column, String val) {
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
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    public <S> MPJLambdaWrapperX<T> inIfPresent(SFunction<S, ?> column, Collection<?> values) {
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
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    public <S> MPJLambdaWrapperX<T> inIfPresent(SFunction<S, ?> column, Object... values) {
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
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    public <S> MPJLambdaWrapperX<T> eqIfPresent(SFunction<S, ?> column, Object val) {
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
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    public <S> MPJLambdaWrapperX<T> neIfPresent(SFunction<S, ?> column, Object val) {
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
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    public <S> MPJLambdaWrapperX<T> gtIfPresent(SFunction<S, ?> column, Object val) {
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
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    public <S> MPJLambdaWrapperX<T> geIfPresent(SFunction<S, ?> column, Object val) {
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
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    public <S> MPJLambdaWrapperX<T> ltIfPresent(SFunction<S, ?> column, Object val) {
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
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    public <S> MPJLambdaWrapperX<T> leIfPresent(SFunction<S, ?> column, Object val) {
        Objects.requireNonNull(column, "Column must not be null");
        if (val != null) {
            super.le(column, val);
        }
        return this;
    }

    /**
     * 如果数组非空，添加 BETWEEN 条件。
     *
     * @param column 字段
     * @param values 值数组，包含起始值和结束值
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    public <S> MPJLambdaWrapperX<T> betweenIfPresent(SFunction<S, ?> column, Object[] values) {
        Objects.requireNonNull(column, "Column must not be null");
        Object val1 = values != null && values.length > 0 ? values[0] : null;
        Object val2 = values != null && values.length > 1 ? values[1] : null;
        return betweenIfPresent(column, val1, val2);
    }

    /**
     * 如果值非空，添加 BETWEEN 条件。
     *
     * @param column 字段
     * @param val1   起始值
     * @param val2   结束值
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    public <S> MPJLambdaWrapperX<T> betweenIfPresent(SFunction<S, ?> column, Object val1, Object val2) {
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

    // ========== 重写父类方法，支持链式调用 ==========

    /**
     * 添加等于 (EQ) 条件，支持条件控制。
     *
     * @param condition 条件是否生效
     * @param column    字段
     * @param val       值
     * @param <X>       字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <X> MPJLambdaWrapperX<T> eq(boolean condition, SFunction<X, ?> column, Object val) {
        super.eq(condition, column, val);
        return this;
    }

    /**
     * 添加等于 (EQ) 条件。
     *
     * @param column 字段
     * @param val    值
     * @param <X>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <X> MPJLambdaWrapperX<T> eq(SFunction<X, ?> column, Object val) {
        super.eq(column, val);
        return this;
    }

    /**
     * 添加降序排序。
     *
     * @param column 字段
     * @param <X>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <X> MPJLambdaWrapperX<T> orderByDesc(SFunction<X, ?> column) {
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
    public MPJLambdaWrapperX<T> last(String lastSql) {
        super.last(lastSql);
        return this;
    }

    /**
     * 添加 IN 条件。
     *
     * @param column 字段
     * @param coll   值集合
     * @param <X>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <X> MPJLambdaWrapperX<T> in(SFunction<X, ?> column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }

    /**
     * 选择指定类的所有字段。
     *
     * @param clazz 目标类
     * @return 当前查询包装器
     */
    @Override
    public MPJLambdaWrapperX<T> selectAll(Class<?> clazz) {
        super.selectAll(clazz);
        return this;
    }

    /**
     * 选择指定类的所有字段，并指定表前缀。
     *
     * @param clazz  目标类
     * @param prefix 表前缀
     * @return 当前查询包装器
     */
    @Override
    public MPJLambdaWrapperX<T> selectAll(Class<?> clazz, String prefix) {
        super.selectAll(clazz, prefix);
        return this;
    }

    /**
     * 选择字段并指定别名。
     *
     * @param column 字段
     * @param alias  别名
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectAs(SFunction<S, ?> column, String alias) {
        super.selectAs(column, alias);
        return this;
    }

    /**
     * 选择字段并指定别名字段。
     *
     * @param column 字段
     * @param alias  别名字段
     * @param <E>    别名字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <E> MPJLambdaWrapperX<T> selectAs(String column, SFunction<E, ?> alias) {
        super.selectAs(column, alias);
        return this;
    }

    /**
     * 选择字段并指定别名字段。
     *
     * @param column 字段
     * @param alias  别名字段
     * @param <S>    字段所属类型
     * @param <X>    别名字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectAs(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectAs(column, alias);
        return this;
    }

    /**
     * 选择字段并指定表索引和别名字段。
     *
     * @param index  表索引
     * @param column 字段
     * @param alias  别名字段
     * @param <E>    字段所属类型
     * @param <X>    别名字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <E, X> MPJLambdaWrapperX<T> selectAs(String index, SFunction<E, ?> column, SFunction<X, ?> alias) {
        super.selectAs(index, column, alias);
        return this;
    }

    /**
     * 选择指定类的字段并映射到目标类。
     *
     * @param source 源类
     * @param tag    目标类
     * @param <E>    源类类型
     * @return 当前查询包装器
     */
    @Override
    public <E> MPJLambdaWrapperX<T> selectAsClass(Class<E> source, Class<?> tag) {
        super.selectAsClass(source, tag);
        return this;
    }

    /**
     * 选择子查询并指定别名。
     *
     * @param clazz    子查询类
     * @param consumer 子查询条件
     * @param alias    别名字段
     * @param <E>      子查询类类型
     * @param <F>      别名字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <E, F> MPJLambdaWrapperX<T> selectSub(Class<E> clazz, Consumer<MPJLambdaWrapper<E>> consumer, SFunction<F, ?> alias) {
        super.selectSub(clazz, consumer, alias);
        return this;
    }

    /**
     * 选择子查询并指定表索引和别名。
     *
     * @param clazz    子查询类
     * @param st       表索引
     * @param consumer 子查询条件
     * @param alias    别名字段
     * @param <E>      子查询类类型
     * @param <F>      别名字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <E, F> MPJLambdaWrapperX<T> selectSub(Class<E> clazz, String st, Consumer<MPJLambdaWrapper<E>> consumer, SFunction<F, ?> alias) {
        super.selectSub(clazz, st, consumer, alias);
        return this;
    }

    /**
     * 选择字段的 COUNT 值。
     *
     * @param column 字段
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectCount(SFunction<S, ?> column) {
        super.selectCount(column);
        return this;
    }

    /**
     * 选择字段的 COUNT 值并指定别名。
     *
     * @param column 字段
     * @param alias  别名
     * @return 当前查询包装器
     */
    @Override
    public MPJLambdaWrapperX<T> selectCount(Object column, String alias) {
        super.selectCount(column, alias);
        return this;
    }

    /**
     * 选择字段的 COUNT 值并指定别名字段。
     *
     * @param column 字段
     * @param alias  别名字段
     * @param <X>    别名字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <X> MPJLambdaWrapperX<T> selectCount(Object column, SFunction<X, ?> alias) {
        super.selectCount(column, alias);
        return this;
    }

    /**
     * 选择字段的 COUNT 值并指定别名。
     *
     * @param column 字段
     * @param alias  别名
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectCount(SFunction<S, ?> column, String alias) {
        super.selectCount(column, alias);
        return this;
    }

    /**
     * 选择字段的 COUNT 值并指定别名字段。
     *
     * @param column 字段
     * @param alias  别名字段
     * @param <S>    字段所属类型
     * @param <X>    别名字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectCount(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectCount(column, alias);
        return this;
    }

    /**
     * 选择字段的 SUM 值。
     *
     * @param column 字段
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectSum(SFunction<S, ?> column) {
        super.selectSum(column);
        return this;
    }

    /**
     * 选择字段的 SUM 值并指定别名。
     *
     * @param column 字段
     * @param alias  别名
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectSum(SFunction<S, ?> column, String alias) {
        super.selectSum(column, alias);
        return this;
    }

    /**
     * 选择字段的 SUM 值并指定别名字段。
     *
     * @param column 字段
     * @param alias  别名字段
     * @param <S>    字段所属类型
     * @param <X>    别名字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectSum(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectSum(column, alias);
        return this;
    }

    /**
     * 选择字段的 MAX 值。
     *
     * @param column 字段
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectMax(SFunction<S, ?> column) {
        super.selectMax(column);
        return this;
    }

    /**
     * 选择字段的 MAX 值并指定别名。
     *
     * @param column 字段
     * @param alias  别名
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectMax(SFunction<S, ?> column, String alias) {
        super.selectMax(column, alias);
        return this;
    }

    /**
     * 选择字段的 MAX 值并指定别名字段。
     *
     * @param column 字段
     * @param alias  别名字段
     * @param <S>    字段所属类型
     * @param <X>    别名字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectMax(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectMax(column, alias);
        return this;
    }

    /**
     * 选择字段的 MIN 值。
     *
     * @param column 字段
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectMin(SFunction<S, ?> column) {
        super.selectMin(column);
        return this;
    }

    /**
     * 选择字段的 MIN 值并指定别名。
     *
     * @param column 字段
     * @param alias  别名
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectMin(SFunction<S, ?> column, String alias) {
        super.selectMin(column, alias);
        return this;
    }

    /**
     * 选择字段的 MIN 值并指定别名字段。
     *
     * @param column 字段
     * @param alias  别名字段
     * @param <S>    字段所属类型
     * @param <X>    别名字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectMin(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectMin(column, alias);
        return this;
    }

    /**
     * 选择字段的 AVG 值。
     *
     * @param column 字段
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectAvg(SFunction<S, ?> column) {
        super.selectAvg(column);
        return this;
    }

    /**
     * 选择字段的 AVG 值并指定别名。
     *
     * @param column 字段
     * @param alias  别名
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectAvg(SFunction<S, ?> column, String alias) {
        super.selectAvg(column, alias);
        return this;
    }

    /**
     * 选择字段的 AVG 值并指定别名字段。
     *
     * @param column 字段
     * @param alias  别名字段
     * @param <S>    字段所属类型
     * @param <X>    别名字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectAvg(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectAvg(column, alias);
        return this;
    }

    /**
     * 选择字段的 LENGTH 值。
     *
     * @param column 字段
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectLen(SFunction<S, ?> column) {
        super.selectLen(column);
        return this;
    }

    /**
     * 选择字段的 LENGTH 值并指定别名。
     *
     * @param column 字段
     * @param alias  别名
     * @param <S>    字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S> MPJLambdaWrapperX<T> selectLen(SFunction<S, ?> column, String alias) {
        super.selectLen(column, alias);
        return this;
    }

    /**
     * 选择字段的 LENGTH 值并指定别名字段。
     *
     * @param column 字段
     * @param alias  别名字段
     * @param <S>    字段所属类型
     * @param <X>    别名字段所属类型
     * @return 当前查询包装器
     */
    @Override
    public <S, X> MPJLambdaWrapperX<T> selectLen(SFunction<S, ?> column, SFunction<X, ?> alias) {
        super.selectLen(column, alias);
        return this;
    }

    /**
     * 添加 LEFT JOIN 连表查询。
     *
     * @param clazz 连表类
     * @param left  左表字段
     * @param right 右表字段
     * @param <A>   左表类型
     * @param <B>   右表类型
     * @return 当前查询包装器
     */
    @Override
    public <A, B> MPJLambdaWrapperX<T> leftJoin(Class<A> clazz, SFunction<A, ?> left, SFunction<B, ?> right) {
        super.leftJoin(clazz, left, right);
        return this;
    }

    /**
     * 添加 RIGHT JOIN 连表查询。
     *
     * @param clazz 连表类
     * @param left  左表字段
     * @param right 右表字段
     * @param <A>   左表类型
     * @param <B>   右表类型
     * @return 当前查询包装器
     */
    @Override
    public <A, B> MPJLambdaWrapperX<T> rightJoin(Class<A> clazz, SFunction<A, ?> left, SFunction<B, ?> right) {
        super.rightJoin(clazz, left, right);
        return this;
    }

    /**
     * 添加 INNER JOIN 连表查询。
     *
     * @param clazz 连表类
     * @param left  左表字段
     * @param right 右表字段
     * @param <A>   左表类型
     * @param <B>   右表类型
     * @return 当前查询包装器
     */
    @Override
    public <A, B> MPJLambdaWrapperX<T> innerJoin(Class<A> clazz, SFunction<A, ?> left, SFunction<B, ?> right) {
        super.innerJoin(clazz, left, right);
        return this;
    }

    /**
     * 添加 LEFT JOIN 连表查询，支持扩展条件。
     *
     * @param clazz 连表类
     * @param left  左表字段
     * @param right 右表字段
     * @param ext   扩展条件
     * @param <A>   左表类型
     * @param <B>   右表类型
     * @return 当前查询包装器
     */
    public <A, B> MPJLambdaWrapperX<T> leftJoin(Class<A> clazz, SFunction<A, ?> left, SFunction<B, ?> right, Consumer<MPJLambdaWrapperX<T>> ext) {
        Objects.requireNonNull(clazz, "Class must not be null");
        Objects.requireNonNull(left, "Left column must not be null");
        Objects.requireNonNull(right, "Right column must not be null");
        super.leftJoin(clazz, left, right);
        if (ext != null) {
            ext.accept(this);
        }
        return this;
    }

    /**
     * 添加 RIGHT JOIN 连表查询，支持扩展条件。
     *
     * @param clazz 连表类
     * @param left  左表字段
     * @param right 右表字段
     * @param ext   扩展条件
     * @param <A>   左表类型
     * @param <B>   右表类型
     * @return 当前查询包装器
     */
    public <A, B> MPJLambdaWrapperX<T> rightJoin(Class<A> clazz, SFunction<A, ?> left, SFunction<B, ?> right, Consumer<MPJLambdaWrapperX<T>> ext) {
        Objects.requireNonNull(clazz, "Class must not be null");
        Objects.requireNonNull(left, "Left column must not be null");
        Objects.requireNonNull(right, "Right column must not be null");
        super.rightJoin(clazz, left, right);
        if (ext != null) {
            ext.accept(this);
        }
        return this;
    }

    /**
     * 添加 INNER JOIN 连表查询，支持扩展条件。
     *
     * @param clazz 连表类
     * @param left  左表字段
     * @param right 右表字段
     * @param ext   扩展条件
     * @param <A>   左表类型
     * @param <B>   右表类型
     * @return 当前查询包装器
     */
    public <A, B> MPJLambdaWrapperX<T> innerJoin(Class<A> clazz, SFunction<A, ?> left, SFunction<B, ?> right, Consumer<MPJLambdaWrapperX<T>> ext) {
        Objects.requireNonNull(clazz, "Class must not be null");
        Objects.requireNonNull(left, "Left column must not be null");
        Objects.requireNonNull(right, "Right column must not be null");
        super.innerJoin(clazz, left, right);
        if (ext != null) {
            ext.accept(this);
        }
        return this;
    }
}
