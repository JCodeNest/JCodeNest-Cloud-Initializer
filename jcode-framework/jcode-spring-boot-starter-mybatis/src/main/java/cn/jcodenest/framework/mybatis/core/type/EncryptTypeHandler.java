package cn.jcodenest.framework.mybatis.core.type;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.extra.spring.SpringUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 基于 AES 加密的 MyBatis 字段类型处理器。
 *
 * <p>
 *     通过配置项 {@code mybatis-plus.encryptor.password} 设置加密密钥，用于对字段进行加密和解密。
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class EncryptTypeHandler extends BaseTypeHandler<String> {

    /**
     * 加密密钥的配置项名称
     */
    private static final String ENCRYPTOR_PROPERTY_NAME = "mybatis-plus.encryptor.password";

    /**
     * AES 加密器，线程安全
     */
    private static volatile AES aes;

    /**
     * 设置非空参数，将字符串加密后存储到数据库。
     *
     * @param ps        预处理语句
     * @param i         参数索引
     * @param parameter 参数值
     * @param jdbcType  JDBC 类型
     * @throws SQLException 如果数据库操作失败
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        Objects.requireNonNull(ps, "PreparedStatement must not be null");
        Objects.requireNonNull(parameter, "Parameter must not be null");
        ps.setString(i, encrypt(parameter));
    }

    /**
     * 从结果集中获取指定列名的值并解密。
     *
     * @param rs         结果集
     * @param columnName 列名
     * @return 解密后的字符串
     * @throws SQLException 如果数据库操作失败
     */
    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Objects.requireNonNull(rs, "ResultSet must not be null");
        Objects.requireNonNull(columnName, "ColumnName must not be null");
        String value = rs.getString(columnName);
        return decrypt(value);
    }

    /**
     * 从结果集中获取指定列索引的值并解密。
     *
     * @param rs          结果集
     * @param columnIndex 列索引
     * @return 解密后的字符串
     * @throws SQLException 如果数据库操作失败
     */
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Objects.requireNonNull(rs, "ResultSet must not be null");
        String value = rs.getString(columnIndex);
        return decrypt(value);
    }

    /**
     * 从存储过程中获取指定列索引的值并解密。
     *
     * @param cs          存储过程调用语句
     * @param columnIndex 列索引
     * @return 解密后的字符串
     * @throws SQLException 如果数据库操作失败
     */
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Objects.requireNonNull(cs, "CallableStatement must not be null");
        String value = cs.getString(columnIndex);
        return decrypt(value);
    }

    /**
     * 解密数据库中的加密字符串。
     *
     * @param value 加密的字符串
     * @return 解密后的字符串，如果输入为空则返回 null
     */
    private static String decrypt(String value) {
        if (value == null) {
            return null;
        }
        return getEncryptor().decryptStr(value);
    }

    /**
     * 加密字符串以存储到数据库。
     *
     * @param rawValue 原始字符串
     * @return 加密后的 Base64 字符串，如果输入为空则返回 null
     */
    public static String encrypt(String rawValue) {
        if (rawValue == null) {
            return null;
        }
        return getEncryptor().encryptBase64(rawValue);
    }

    /**
     * 获取 AES 加密器，线程安全地进行懒加载。
     *
     * @return AES 加密器
     * @throws IllegalArgumentException 如果加密密钥配置为空
     */
    private static AES getEncryptor() {
        if (aes == null) {
            synchronized (EncryptTypeHandler.class) {
                if (aes == null) {
                    String password = SpringUtil.getProperty(ENCRYPTOR_PROPERTY_NAME);
                    if (password == null || password.isBlank()) {
                        throw new IllegalArgumentException("Configuration property 'mybatis-plus.encryptor.password' must not be empty");
                    }
                    aes = SecureUtil.aes(password.getBytes());
                }
            }
        }
        return aes;
    }
}
