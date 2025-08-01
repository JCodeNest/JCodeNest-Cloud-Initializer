package cn.jcodenest.framework.mybatis.core.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.util.Collections.emptySet;

/**
 * Set<Long> 的 MyBatis 类型处理器，将 Set<Long> 转换为数据库的 VARCHAR 类型。
 *
 * <p>
 * 使用逗号分隔符将长整型集合存储为字符串，并在读取时解析回 Set<Long>。
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class LongSetTypeHandler implements TypeHandler<Set<Long>> {

    /**
     * 集合元素的分隔符
     */
    private static final String COMMA = ",";

    /**
     * 将 Set<Long> 转换为逗号分隔的字符串并设置到数据库。
     *
     * @param ps        预处理语句
     * @param i         参数索引
     * @param parameter 长整型集合
     * @param jdbcType  JDBC 类型
     * @throws SQLException 如果数据库操作失败
     */
    @Override
    public void setParameter(PreparedStatement ps, int i, Set<Long> parameter, JdbcType jdbcType) throws SQLException {
        Objects.requireNonNull(ps, "PreparedStatement must not be null");
        String value = parameter == null || parameter.isEmpty() ? null : String.join(COMMA, parameter.stream().map(String::valueOf).toList());
        ps.setString(i, value);
    }

    /**
     * 从结果集中获取指定列名的值并解析为 Set<Long>。
     *
     * @param rs         结果集
     * @param columnName 列名
     * @return 长整型集合
     * @throws SQLException 如果数据库操作失败
     */
    @Override
    public Set<Long> getResult(ResultSet rs, String columnName) throws SQLException {
        Objects.requireNonNull(rs, "ResultSet must not be null");
        Objects.requireNonNull(columnName, "ColumnName must not be null");
        String value = rs.getString(columnName);
        return getResult(value);
    }

    /**
     * 从结果集中获取指定列索引的值并解析为 Set<Long>。
     *
     * @param rs          结果集
     * @param columnIndex 列索引
     * @return 长整型集合
     * @throws SQLException 如果数据库操作失败
     */
    @Override
    public Set<Long> getResult(ResultSet rs, int columnIndex) throws SQLException {
        Objects.requireNonNull(rs, "ResultSet must not be null");
        String value = rs.getString(columnIndex);
        return getResult(value);
    }

    /**
     * 从存储过程中获取指定列索引的值并解析为 Set<Long>。
     *
     * @param cs          存储过程调用语句
     * @param columnIndex 列索引
     * @return 长整型集合
     * @throws SQLException 如果数据库操作失败
     */
    @Override
    public Set<Long> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        Objects.requireNonNull(cs, "CallableStatement must not be null");
        String value = cs.getString(columnIndex);
        return getResult(value);
    }

    /**
     * 将逗号分隔的字符串解析为 Set<Long>。
     *
     * @param value 逗号分隔的字符串
     * @return 长整型集合，如果输入为空或无效则返回空集合
     */
    private Set<Long> getResult(String value) {
        if (value == null || value.isBlank()) {
            return emptySet();
        }

        try {
            return Set.of(Arrays.stream(value.split(COMMA))
                    .filter(s -> !s.trim().isEmpty())
                    .map(Long::parseLong)
                    .toArray(Long[]::new));
        } catch (NumberFormatException e) {
            // 如果解析失败，返回空集合以避免异常
            return emptySet();
        }
    }
}
