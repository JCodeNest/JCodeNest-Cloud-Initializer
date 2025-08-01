package cn.jcodenest.framework.test.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

/**
 * SQL 初始化的测试 Configuration
 *
 * <p>
 * ❓为什么不使用 org.springframework.boot.autoconfigure.sql.init.DataSourceInitializationConfiguration？
 * 因为我们在单元测试会使用 spring.main.lazy-initialization 为 true，开启延迟加载。此时，会导致 DataSourceInitializationConfiguration 初始化。
 * 不过当前类的实现代码，基本是复制 DataSourceInitializationConfiguration 的。
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(AbstractScriptDatabaseInitializer.class)
@ConditionalOnSingleCandidate(DataSource.class)
@ConditionalOnClass(name = "org.springframework.jdbc.datasource.init.DatabasePopulator")
@Lazy(value = false) // 禁止延迟加载
@EnableConfigurationProperties(SqlInitializationProperties.class)
public class SqlInitializationTestConfiguration {

    /**
     * 创建 DataSourceScriptDatabaseInitializer Bean
     *
     * @param dataSource            数据源
     * @param initializationProperties SQL 初始化属性
     * @return DataSourceScriptDatabaseInitializer
     */
    @Bean
    public DataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(DataSource dataSource, SqlInitializationProperties initializationProperties) {
        DatabaseInitializationSettings settings = createFrom(initializationProperties);
        return new DataSourceScriptDatabaseInitializer(dataSource, settings);
    }

    // 复制自 DataSourceInitializationConfiguration
    static DatabaseInitializationSettings createFrom(SqlInitializationProperties properties) {
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(properties.getSchemaLocations());
        settings.setDataLocations(properties.getDataLocations());
        settings.setContinueOnError(properties.isContinueOnError());
        settings.setSeparator(properties.getSeparator());
        settings.setEncoding(properties.getEncoding());
        settings.setMode(properties.getMode());
        return settings;
    }
}
