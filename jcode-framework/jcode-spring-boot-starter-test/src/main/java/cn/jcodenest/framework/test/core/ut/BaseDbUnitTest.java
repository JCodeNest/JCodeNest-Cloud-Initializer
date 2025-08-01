package cn.jcodenest.framework.test.core.ut;

import cn.hutool.extra.spring.SpringUtil;
import cn.jcodenest.framework.datasource.config.JCodeDataSourceAutoConfiguration;
import cn.jcodenest.framework.mybatis.config.JCodeMybatisAutoConfiguration;
import cn.jcodenest.framework.test.config.SqlInitializationTestConfiguration;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.github.yulichang.autoconfigure.MybatisPlusJoinAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

/**
 * 依赖内存 DB 的单元测试
 *
 * <p>
 * 注意: Service 层同样适用。对于 Service 层的单元测试，针对自己模块的 Mapper 走的是 H2 内存数据库，
 * 针对别的模块的 Service 走的是 Mock 方法.
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = BaseDbUnitTest.Application.class)
@ActiveProfiles("unit-test") // 设置使用 application-unit-test 配置文件
@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) // 每个单元测试结束后清理 DB
public class BaseDbUnitTest {

    @Import({
            // DB 配置类
            JCodeDataSourceAutoConfiguration.class,               // 自己的 DB 配置类
            DataSourceAutoConfiguration.class,                    // Spring DB 自动配置类
            DataSourceTransactionManagerAutoConfiguration.class,  // Spring 事务自动配置类
            DruidDataSourceAutoConfigure.class,                   // Druid 自动配置类
            SqlInitializationTestConfiguration.class,             // SQL 初始化

            // MyBatis 配置类
            JCodeMybatisAutoConfiguration.class,                  // 自己的 MyBatis 配置类
            MybatisPlusAutoConfiguration.class,                   // MyBatis 的自动配置类
            MybatisPlusJoinAutoConfiguration.class,               // MyBatis 的Join配置类

            // 其它配置类
            SpringUtil.class
    })
    public static class Application {
    }
}
