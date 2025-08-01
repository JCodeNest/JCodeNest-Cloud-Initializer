package cn.jcodenest.framework.mybatis.config;

import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.mybatis.core.handler.DefaultDBFieldHandler;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.*;
import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import com.baomidou.mybatisplus.extension.parser.cache.JdkSerialCaffeineJsqlParseCache;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * MyBaits 配置类
 *
 * <p>
 *     用于配置 MyBatis-Plus 的插件、自动填充处理器和主键生成器。
 *     优先于 MyBatis-Plus 官方自动配置，以确保 Mapper 扫描正确执行。
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration(before = MybatisPlusAutoConfiguration.class)
@MapperScan(
        value = "${jcode.info.base-package}",
        annotationClass = Mapper.class,
        lazyInitialization = "${mybatis.lazy-initialization:false}"
)
public class JCodeMybatisAutoConfiguration {

    /*
      静态初始化块，配置动态 SQL 解析缓存以优化性能。
     */
    static {
        // 设置本地缓存以加速动态 SQL 解析，支持复杂租户 SQL，缓存 1024 条记录，5 秒过期
        JsqlParserGlobal.setJsqlParseCache(
                new JdkSerialCaffeineJsqlParseCache(cache ->
                        cache.maximumSize(1024).expireAfterWrite(5, TimeUnit.SECONDS))
        );
    }

    /**
     * 配置 MyBatis-Plus 拦截器，添加分页插件。
     *
     * @return 配置好的 MyBatis-Plus 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 添加分页拦截器
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }

    /**
     * 配置默认的字段自动填充处理器。
     *
     * @return 默认的字段填充处理器
     */
    @Bean
    public MetaObjectHandler defaultMetaObjectHandler() {
        return new DefaultDBFieldHandler();
    }

    /**
     * 根据数据库类型配置主键生成器，仅当 id-type 配置为 INPUT 时生效。
     *
     * @param environment Spring 的环境配置对象
     * @return 数据库对应的主键生成器
     * @throws IllegalArgumentException 如果找不到合适的主键生成器
     */
    @Bean
    @ConditionalOnProperty(prefix = "mybatis-plus.global-config.db-config", name = "id-type", havingValue = "INPUT")
    public IKeyGenerator keyGenerator(ConfigurableEnvironment environment) {
        Objects.requireNonNull(environment, "Environment must not be null");

        DbType dbType = IdTypeEnvironmentPostProcessor.getDbType(environment);
        if (dbType == null) {
            throw new IllegalArgumentException("Unable to determine database type for key generator");
        }

        // 根据数据库类型选择主键生成器
        return switch (dbType) {
            case POSTGRE_SQL -> new PostgreKeyGenerator();
            case ORACLE, ORACLE_12C -> new OracleKeyGenerator();
            case H2 -> new H2KeyGenerator();
            case KINGBASE_ES -> new KingbaseKeyGenerator();
            case DM -> new DmKeyGenerator();
            default -> throw new IllegalArgumentException(String.format("No suitable IKeyGenerator found for DbType %s", dbType));
        };
    }
}
