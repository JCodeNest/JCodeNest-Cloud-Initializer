package cn.jcodenest.framework.mybatis.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.common.pojo.PageParam;
import cn.jcodenest.framework.common.pojo.SortingField;
import cn.jcodenest.framework.mybatis.core.enums.DbTypeEnum;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * MyBatis 工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyBatisUtils {

    /**
     * MySQL 转义字符
     */
    private static final String MYSQL_ESCAPE_CHARACTER = "`";

    /**
     * 构建 Page 对象
     *
     * @param pageParam 分页参数
     * @return Page 对象
     */
    public static <T> Page<T> buildPage(PageParam pageParam) {
        return buildPage(pageParam, null);
    }

    /**
     * 构建 Page 对象
     *
     * @param pageParam     分页参数
     * @param sortingFields 排序字段
     * @return Page 对象
     */
    public static <T> Page<T> buildPage(PageParam pageParam, Collection<SortingField> sortingFields) {
        // 页码 + 数量
        Page<T> page = new Page<>(pageParam.getPageNo(), pageParam.getPageSize());

        // 排序字段
        if (CollUtil.isNotEmpty(sortingFields)) {
            for (SortingField sortingField : sortingFields) {
                page.addOrder(
                        new OrderItem()
                                .setAsc(SortingField.ORDER_ASC.equals(sortingField.getOrder()))
                                .setColumn(StrUtil.toUnderlineCase(sortingField.getField()))
                );
            }
        }

        return page;
    }

    /**
     * 添加排序字段
     *
     * @param wrapper       查询条件
     * @param sortingFields 排序字段
     * @param <T>           实体类型
     */
    public static <T> void addOrder(Wrapper<T> wrapper, Collection<SortingField> sortingFields) {
        if (CollUtil.isEmpty(sortingFields)) {
            return;
        }

        QueryWrapper<T> query = (QueryWrapper<T>) wrapper;
        for (SortingField sortingField : sortingFields) {
            query.orderBy(
                    true,
                    SortingField.ORDER_ASC.equals(sortingField.getOrder()),
                    StrUtil.toUnderlineCase(sortingField.getField())
            );
        }
    }

    /**
     * 将拦截器添加到链中
     * <p>
     * 由于 MybatisPlusInterceptor 不支持添加拦截器，所以只能全量设置
     *
     * @param interceptor 链
     * @param inner       拦截器
     * @param index       位置
     */
    public static void addInterceptor(MybatisPlusInterceptor interceptor, InnerInterceptor inner, int index) {
        List<InnerInterceptor> inners = new ArrayList<>(interceptor.getInterceptors());
        inners.add(index, inner);
        interceptor.setInterceptors(inners);
    }

    /**
     * 获得 Table 对应的表名
     * <p>
     * 兼容 MySQL 转义表名 `t_xxx`
     *
     * @param table 表
     * @return 去除转移字符后的表名
     */
    public static String getTableName(Table table) {
        String tableName = table.getName();
        if (tableName.startsWith(MYSQL_ESCAPE_CHARACTER) && tableName.endsWith(MYSQL_ESCAPE_CHARACTER)) {
            tableName = tableName.substring(1, tableName.length() - 1);
        }

        return tableName;
    }

    /**
     * 构建 Column 对象
     *
     * @param tableName  表名
     * @param tableAlias 别名
     * @param column     字段名
     * @return Column 对象
     */
    public static Column buildColumn(String tableName, Alias tableAlias, String column) {
        if (tableAlias != null) {
            tableName = tableAlias.getName();
        }

        return new Column(tableName + StringPool.DOT + column);
    }

    /**
     * 跨数据库的 find_in_set 实现
     *
     * @param column 字段名称
     * @param value  查询值(不带单引号)
     * @return sql
     */
    public static String findInSet(String column, Object value) {
        DbType dbType = JdbcUtils.getDbType();
        return DbTypeEnum.getFindInSetTemplate(dbType)
                .replace("#{column}", column)
                .replace("#{value}", StrUtil.toString(value));
    }

    /**
     * 将驼峰命名转换为下划线命名
     * <p>
     * 使用场景：
     * 1. <a href="https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/1357/files">fix:修复"商品统计聚合函数的别名与排序字段不符"导致的 SQL 异常</a>
     * </p>
     *
     * @param func 字段名函数(驼峰命名)
     * @return 字段名(下划线命名)
     */
    public static <T> String toUnderlineCase(Func1<T, ?> func) {
        String fieldName = LambdaUtil.getFieldName(func);
        return StrUtil.toUnderlineCase(fieldName);
    }
}
