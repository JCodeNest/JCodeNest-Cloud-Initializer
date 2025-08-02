package cn.jcodenest.framework.datapermission.config;

import cn.jcodenest.framework.datapermission.core.aop.DataPermissionAnnotationAdvisor;
import cn.jcodenest.framework.datapermission.core.db.DataPermissionRuleHandler;
import cn.jcodenest.framework.datapermission.core.rule.DataPermissionRule;
import cn.jcodenest.framework.datapermission.core.rule.DataPermissionRuleFactory;
import cn.jcodenest.framework.datapermission.core.rule.DataPermissionRuleFactoryImpl;
import cn.jcodenest.framework.mybatis.core.util.MyBatisUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 数据权限的自动配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
public class JCodeDataPermissionAutoConfiguration {

    /**
     * 创建数据权限规则工厂类
     *
     * @param rules 数据权限规则列表
     * @return 数据权限规则工厂类
     */
    @Bean
    public DataPermissionRuleFactory dataPermissionRuleFactory(List<DataPermissionRule> rules) {
        return new DataPermissionRuleFactoryImpl(rules);
    }

    /**
     * 创建数据权限规则
     *
     * @return 数据权限规则列表
     */
    @Bean
    public DataPermissionRuleHandler dataPermissionRuleHandler(MybatisPlusInterceptor interceptor, DataPermissionRuleFactory ruleFactory) {
        // 创建 DataPermissionInterceptor 拦截器
        DataPermissionRuleHandler handler = new DataPermissionRuleHandler(ruleFactory);
        DataPermissionInterceptor inner = new DataPermissionInterceptor(handler);

        // 添加到 interceptor 中
        // 需要加在首个，主要是为了在分页插件前面, 这个是 MyBatis Plus 的规定
        MyBatisUtils.addInterceptor(interceptor, inner, 0);
        return handler;
    }

    /**
     * 创建 DataPermissionAnnotationAdvisor
     */
    @Bean
    public DataPermissionAnnotationAdvisor dataPermissionAnnotationAdvisor() {
        return new DataPermissionAnnotationAdvisor();
    }
}
