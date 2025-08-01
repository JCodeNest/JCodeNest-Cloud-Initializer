package cn.jcodenest.framework.mybatis.config;

import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.common.util.collection.SetUtils;
import cn.jcodenest.framework.mybatis.core.util.JdbcUtils;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Objects;
import java.util.Set;

/**
 * 配置 MyBatis-Plus 主键类型，根据 PRIMARY 数据源的数据库类型自动设置 IdType。
 * 当 IdType 为 {@link IdType#NONE} 时，根据数据库类型动态配置为主键自增或用户输入。
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class IdTypeEnvironmentPostProcessor implements EnvironmentPostProcessor {

    /**
     * MyBatis-Plus 主键类型配置的属性键
     */
    private static final String ID_TYPE_KEY = "mybatis-plus.global-config.db-config.id-type";

    /**
     * 动态数据源配置的属性键前缀
     */
    private static final String DATASOURCE_DYNAMIC_KEY = "spring.datasource.dynamic";

    /**
     * Quartz JobStore 驱动类的属性键
     */
    private static final String QUARTZ_JOB_STORE_DRIVER_KEY = "spring.quartz.properties.org.quartz.jobStore.driverDelegateClass";

    /**
     * 支持用户输入主键的数据库类型集合
     */
    private static final Set<DbType> INPUT_ID_TYPES = SetUtils.asSet(
            DbType.ORACLE,
            DbType.ORACLE_12C,
            DbType.POSTGRE_SQL,
            DbType.KINGBASE_ES,
            DbType.DB2,
            DbType.H2
    );

    /**
     * 处理 Spring 环境配置，根据数据源的数据库类型自动设置 MyBatis-Plus 的主键类型。
     *
     * @param environment Spring 的环境配置对象
     * @param application Spring 应用对象
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Objects.requireNonNull(environment, "Environment must not be null");
        Objects.requireNonNull(application, "Application must not be null");

        // 获取数据库类型，若为空则不处理
        DbType dbType = getDbType(environment);
        if (dbType == null) {
            return;
        }

        // 设置 Quartz JobStore 驱动
        setJobStoreDriverIfPresent(environment, dbType);

        // 检查当前主键类型，若非 NONE 则不处理
        IdType idType = getIdType(environment);
        if (idType != IdType.NONE) {
            return;
        }

        // 对于支持用户输入主键的数据库，设置为 INPUT: 适合 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库
        if (INPUT_ID_TYPES.contains(dbType)) {
            setIdType(environment, IdType.INPUT);
        } else {
            // 其他数据库设置为自增主键: 适合 MySQL、DM 达梦等直接自增的数据库
            setIdType(environment, IdType.AUTO);
        }
    }

    /**
     * 获取 MyBatis-Plus 配置的主键类型。
     *
     * @param environment Spring 的环境配置对象
     * @return 主键类型，若未配置则返回 null
     */
    public IdType getIdType(ConfigurableEnvironment environment) {
        return environment.getProperty(ID_TYPE_KEY, IdType.class);
    }

    /**
     * 设置 MyBatis-Plus 的主键类型，并记录日志。
     *
     * @param environment Spring 的环境配置对象
     * @param idType 要设置的主键类型
     */
    public void setIdType(ConfigurableEnvironment environment, IdType idType) {
        environment.getSystemProperties().put(ID_TYPE_KEY, idType);
        log.info("[setIdType][修改 MyBatis Plus 的 idType 为({})]", idType);
    }

    /**
     * 根据数据库类型设置 Quartz JobStore 的驱动类（如果未设置）。
     *
     * @param environment Spring 的环境配置对象
     * @param dbType 数据库类型
     */
    public void setJobStoreDriverIfPresent(ConfigurableEnvironment environment, DbType dbType) {
        String driverClass = environment.getProperty(QUARTZ_JOB_STORE_DRIVER_KEY);
        if (StrUtil.isNotEmpty(driverClass)) {
            return;
        }

        // 使用 switch 表达式选择驱动类
        driverClass = switch (dbType) {
            case POSTGRE_SQL -> "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate";
            case ORACLE, ORACLE_12C -> "org.quartz.impl.jdbcjobstore.oracle.OracleDelegate";
            case SQL_SERVER, SQL_SERVER2005 -> "org.quartz.impl.jdbcjobstore.MSSQLDelegate";
            case DM, KINGBASE_ES -> "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
            default -> null;
        };

        // 设置驱动类（如果存在）
        if (StrUtil.isNotEmpty(driverClass)) {
            environment.getSystemProperties().put(QUARTZ_JOB_STORE_DRIVER_KEY, driverClass);
        }
    }

    /**
     * 获取主数据源的数据库类型。
     *
     * @param environment Spring 的环境配置对象
     * @return 数据库类型，若无法获取则返回 null
     */
    public static DbType getDbType(ConfigurableEnvironment environment) {
        String primary = environment.getProperty(DATASOURCE_DYNAMIC_KEY + "." + "primary");
        if (StrUtil.isEmpty(primary)) {
            return null;
        }

        String url = environment.getProperty(DATASOURCE_DYNAMIC_KEY + ".datasource." + primary + ".url");
        if (StrUtil.isEmpty(url)) {
            return null;
        }

        // 通过 JDBC URL 获取数据库类型
        return JdbcUtils.getDbType(url);
    }
}
