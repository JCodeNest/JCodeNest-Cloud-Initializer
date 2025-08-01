package cn.jcodenest.framework.mybatis.core.mapper;

import cn.jcodenest.framework.common.pojo.PageParam;
import cn.jcodenest.framework.common.pojo.PageResult;
import cn.jcodenest.framework.common.pojo.SortablePageParam;
import cn.jcodenest.framework.common.pojo.SortingField;
import cn.jcodenest.framework.mybatis.core.util.JdbcUtils;
import cn.jcodenest.framework.mybatis.core.util.MyBatisUtils;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.interfaces.MPJBaseJoin;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 扩展 MyBatis-Plus 的 BaseMapper 接口，提供增强的 CRUD 和连表查询能力。
 *
 * <p>
 * 继承 {@link BaseMapper} 提供基础 CRUD 功能，继承 {@link MPJBaseMapper} 提供连表 Join 功能。
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public interface BaseMapperX<T> extends MPJBaseMapper<T> {

    /**
     * 分页查询，支持排序字段。
     *
     * @param pageParam    分页参数
     * @param queryWrapper 查询条件
     * @return 分页结果
     */
    default PageResult<T> selectPage(SortablePageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        return selectPage(pageParam, pageParam.getSortingFields(), queryWrapper);
    }

    /**
     * 分页查询，不包含排序字段。
     *
     * @param pageParam    分页参数
     * @param queryWrapper 查询条件
     * @return 分页结果
     */
    default PageResult<T> selectPage(PageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        return selectPage(pageParam, null, queryWrapper);
    }

    /**
     * 分页查询，支持自定义排序字段。
     *
     * @param pageParam     分页参数
     * @param sortingFields 排序字段集合
     * @param queryWrapper  查询条件
     * @return 分页结果
     */
    default PageResult<T> selectPage(PageParam pageParam, Collection<SortingField> sortingFields, @Param("ew") Wrapper<T> queryWrapper) {
        Objects.requireNonNull(pageParam, "PageParam must not be null");
        Objects.requireNonNull(queryWrapper, "QueryWrapper must not be null");

        // 不分页时直接查询全部记录
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageSize())) {
            MyBatisUtils.addOrder(queryWrapper, sortingFields);
            List<T> list = selectList(queryWrapper);
            return new PageResult<>(list, (long) list.size());
        }

        // 构建分页对象并执行查询
        IPage<T> mpPage = MyBatisUtils.buildPage(pageParam, sortingFields);
        selectPage(mpPage, queryWrapper);
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

    /**
     * 连表分页查询，支持指定返回类型。
     *
     * @param pageParam     分页参数
     * @param clazz         返回结果类型
     * @param lambdaWrapper 连表查询条件
     * @param <D>           返回结果泛型
     * @return 分页结果
     */
    default <D> PageResult<D> selectJoinPage(PageParam pageParam, Class<D> clazz, MPJLambdaWrapper<T> lambdaWrapper) {
        Objects.requireNonNull(pageParam, "PageParam must not be null");
        Objects.requireNonNull(clazz, "Class must not be null");
        Objects.requireNonNull(lambdaWrapper, "LambdaWrapper must not be null");

        // 不分页时直接查询全部记录
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageSize())) {
            List<D> list = selectJoinList(clazz, lambdaWrapper);
            return new PageResult<>(list, (long) list.size());
        }

        // 构建分页对象并执行连表查询
        IPage<D> mpPage = MyBatisUtils.buildPage(pageParam);
        selectJoinPage(mpPage, clazz, lambdaWrapper);
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

    /**
     * 连表分页查询，支持指定返回类型和连表查询条件。
     *
     * @param pageParam        分页参数
     * @param resultTypeClass  返回结果类型
     * @param joinQueryWrapper 连表查询条件
     * @param <DTO>            返回结果泛型
     * @return 分页结果
     */
    default <DTO> PageResult<DTO> selectJoinPage(PageParam pageParam, Class<DTO> resultTypeClass, MPJBaseJoin<T> joinQueryWrapper) {
        Objects.requireNonNull(pageParam, "PageParam must not be null");
        Objects.requireNonNull(resultTypeClass, "ResultTypeClass must not be null");
        Objects.requireNonNull(joinQueryWrapper, "JoinQueryWrapper must not be null");

        // 构建分页对象并执行连表查询
        IPage<DTO> mpPage = MyBatisUtils.buildPage(pageParam);
        selectJoinPage(mpPage, resultTypeClass, joinQueryWrapper);
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

    /**
     * 根据单个字段查询单条记录。
     *
     * @param field 字段名
     * @param value 字段值
     * @return 匹配的单条记录
     */
    default T selectOne(String field, Object value) {
        Objects.requireNonNull(field, "Field must not be null");
        return selectOne(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据单个 Lambda 字段查询单条记录。
     *
     * @param field Lambda 字段
     * @param value 字段值
     * @return 匹配的单条记录
     */
    default T selectOne(SFunction<T, ?> field, Object value) {
        Objects.requireNonNull(field, "Field must not be null");
        return selectOne(new LambdaQueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据两个字段查询单条记录。
     *
     * @param field1 字段名 1
     * @param value1 字段值 1
     * @param field2 字段名 2
     * @param value2 字段值 2
     * @return 匹配的单条记录
     */
    default T selectOne(String field1, Object value1, String field2, Object value2) {
        Objects.requireNonNull(field1, "Field1 must not be null");
        Objects.requireNonNull(field2, "Field2 must not be null");
        return selectOne(new QueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    /**
     * 根据两个 Lambda 字段查询单条记录。
     *
     * @param field1 Lambda 字段 1
     * @param value1 字段值 1
     * @param field2 Lambda 字段 2
     * @param value2 字段值 2
     * @return 匹配的单条记录
     */
    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        Objects.requireNonNull(field1, "Field1 must not be null");
        Objects.requireNonNull(field2, "Field2 must not be null");
        return selectOne(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    /**
     * 根据三个 Lambda 字段查询单条记录。
     *
     * @param field1 Lambda 字段 1
     * @param value1 字段值 1
     * @param field2 Lambda 字段 2
     * @param value2 字段值 2
     * @param field3 Lambda 字段 3
     * @param value3 字段值 3
     * @return 匹配的单条记录
     */
    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2, SFunction<T, ?> field3, Object value3) {
        Objects.requireNonNull(field1, "Field1 must not be null");
        Objects.requireNonNull(field2, "Field2 must not be null");
        Objects.requireNonNull(field3, "Field3 must not be null");
        return selectOne(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2).eq(field3, value3));
    }

    /**
     * 获取满足条件的首条记录，适用于并发场景避免 selectOne 报错。
     *
     * @param field Lambda 字段
     * @param value 字段值
     * @return 首条匹配记录
     */
    default T selectFirstOne(SFunction<T, ?> field, Object value) {
        Objects.requireNonNull(field, "Field must not be null");
        List<T> list = selectList(new LambdaQueryWrapper<T>().eq(field, value));
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 获取满足两个条件的首条记录。
     *
     * @param field1 Lambda 字段 1
     * @param value1 字段值 1
     * @param field2 Lambda 字段 2
     * @param value2 字段值 2
     * @return 首条匹配记录
     */
    default T selectFirstOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        Objects.requireNonNull(field1, "Field1 must not be null");
        Objects.requireNonNull(field2, "Field2 must not be null");
        List<T> list = selectList(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2));
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 获取满足三个条件的首条记录。
     *
     * @param field1 Lambda 字段 1
     * @param value1 字段值 1
     * @param field2 Lambda 字段 2
     * @param value2 字段值 2
     * @param field3 Lambda 字段 3
     * @param value3 字段值 3
     * @return 首条匹配记录
     */
    default T selectFirstOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2, SFunction<T, ?> field3, Object value3) {
        Objects.requireNonNull(field1, "Field1 must not be null");
        Objects.requireNonNull(field2, "Field2 must not be null");
        Objects.requireNonNull(field3, "Field3 must not be null");
        List<T> list = selectList(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2).eq(field3, value3));
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 查询记录总数，无条件。
     *
     * @return 记录总数
     */
    default Long selectCount() {
        return selectCount(new QueryWrapper<>());
    }

    /**
     * 根据单个字段查询记录总数。
     *
     * @param field 字段名
     * @param value 字段值
     * @return 记录总数
     */
    default Long selectCount(String field, Object value) {
        Objects.requireNonNull(field, "Field must not be null");
        return selectCount(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据单个 Lambda 字段查询记录总数。
     *
     * @param field Lambda 字段
     * @param value 字段值
     * @return 记录总数
     */
    default Long selectCount(SFunction<T, ?> field, Object value) {
        Objects.requireNonNull(field, "Field must not be null");
        return selectCount(new LambdaQueryWrapper<T>().eq(field, value));
    }

    /**
     * 查询所有记录，无条件。
     *
     * @return 记录列表
     */
    default List<T> selectList() {
        return selectList(new QueryWrapper<>());
    }

    /**
     * 根据单个字段查询记录列表。
     *
     * @param field 字段名
     * @param value 字段值
     * @return 记录列表
     */
    default List<T> selectList(String field, Object value) {
        Objects.requireNonNull(field, "Field must not be null");
        return selectList(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据单个 Lambda 字段查询记录列表。
     *
     * @param field Lambda 字段
     * @param value 字段值
     * @return 记录列表
     */
    default List<T> selectList(SFunction<T, ?> field, Object value) {
        Objects.requireNonNull(field, "Field must not be null");
        return selectList(new LambdaQueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据字段和值集合查询记录列表。
     *
     * @param field  字段名
     * @param values 字段值集合
     * @return 记录列表
     */
    default List<T> selectList(String field, Collection<?> values) {
        Objects.requireNonNull(field, "Field must not be null");
        return values == null || values.isEmpty() ? List.of() : selectList(new QueryWrapper<T>().in(field, values));
    }

    /**
     * 根据 Lambda 字段和值集合查询记录列表。
     *
     * @param field  Lambda 字段
     * @param values 字段值集合
     * @return 记录列表
     */
    default List<T> selectList(SFunction<T, ?> field, Collection<?> values) {
        Objects.requireNonNull(field, "Field must not be null");
        return values == null || values.isEmpty() ? List.of() : selectList(new LambdaQueryWrapper<T>().in(field, values));
    }

    /**
     * 根据两个 Lambda 字段查询记录列表。
     *
     * @param field1 Lambda 字段 1
     * @param value1 字段值 1
     * @param field2 Lambda 字段 2
     * @param value2 字段值 2
     * @return 记录列表
     */
    default List<T> selectList(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        Objects.requireNonNull(field1, "Field1 must not be null");
        Objects.requireNonNull(field2, "Field2 must not be null");
        return selectList(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    /**
     * 批量插入记录，适合大量数据插入。
     *
     * @param entities 实体集合
     * @return 是否插入成功
     */
    default boolean insertBatch(Collection<T> entities) {
        Objects.requireNonNull(entities, "Entities must not be null");
        if (entities.isEmpty()) {
            return false;
        }

        // SQL Server 批量插入需逐条处理以避免获取 ID 报错
        DbType dbType = JdbcUtils.getDbType();
        if (JdbcUtils.isSQLServer(dbType)) {
            entities.forEach(this::insert);
            return true;
        }

        return Db.saveBatch(entities);
    }

    /**
     * 批量插入记录，指定每批插入数量。
     *
     * @param entities 实体集合
     * @param size     每批插入数量
     * @return 是否插入成功
     */
    default boolean insertBatch(Collection<T> entities, int size) {
        Objects.requireNonNull(entities, "Entities must not be null");
        if (entities.isEmpty()) {
            return false;
        }

        // SQL Server 批量插入需逐条处理以避免获取 ID 报错
        DbType dbType = JdbcUtils.getDbType();
        if (JdbcUtils.isSQLServer(dbType)) {
            entities.forEach(this::insert);
            return true;
        }

        return Db.saveBatch(entities, size);
    }

    /**
     * 批量更新记录。
     *
     * @param update 更新的实体对象
     * @return 更新的记录数
     */
    default int updateBatch(T update) {
        Objects.requireNonNull(update, "Update entity must not be null");
        return update(update, new QueryWrapper<>());
    }

    /**
     * 批量更新记录。
     *
     * @param entities 实体集合
     * @return 是否更新成功
     */
    default boolean updateBatch(Collection<T> entities) {
        Objects.requireNonNull(entities, "Entities must not be null");
        return !entities.isEmpty() && Db.updateBatchById(entities);
    }

    /**
     * 批量更新记录，指定每批更新数量。
     *
     * @param entities 实体集合
     * @param size     每批更新数量
     * @return 是否更新成功
     */
    default boolean updateBatch(Collection<T> entities, int size) {
        Objects.requireNonNull(entities, "Entities must not be null");
        return !entities.isEmpty() && Db.updateBatchById(entities, size);
    }

    /**
     * 根据字段删除记录。
     *
     * @param field 字段名
     * @param value 字段值
     * @return 删除的记录数
     */
    default int delete(String field, String value) {
        Objects.requireNonNull(field, "Field must not be null");
        Objects.requireNonNull(value, "Value must not be null");
        return delete(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据 Lambda 字段删除记录。
     *
     * @param field Lambda 字段
     * @param value 字段值
     * @return 删除的记录数
     */
    default int delete(SFunction<T, ?> field, Object value) {
        Objects.requireNonNull(field, "Field must not be null");
        return delete(new LambdaQueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据 Lambda 字段和值集合批量删除记录。
     *
     * @param field  Lambda 字段
     * @param values 字段值集合
     * @return 删除的记录数
     */
    default int deleteBatch(SFunction<T, ?> field, Collection<?> values) {
        Objects.requireNonNull(field, "Field must not be null");
        return values == null || values.isEmpty() ? 0 : delete(new LambdaQueryWrapper<T>().in(field, values));
    }
}
