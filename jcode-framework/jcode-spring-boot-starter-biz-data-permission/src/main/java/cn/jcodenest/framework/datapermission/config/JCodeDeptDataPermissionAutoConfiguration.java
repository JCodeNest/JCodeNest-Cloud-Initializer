package cn.jcodenest.framework.datapermission.config;

import cn.hutool.extra.spring.SpringUtil;
import cn.jcodenest.framework.common.biz.system.permission.PermissionCommonApi;
import cn.jcodenest.framework.datapermission.core.rule.dept.DeptDataPermissionRule;
import cn.jcodenest.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import cn.jcodenest.framework.security.core.LoginUser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 基于部门的数据权限自动配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
@ConditionalOnClass(LoginUser.class)
@ConditionalOnBean(value = {PermissionCommonApi.class, DeptDataPermissionRuleCustomizer.class})
public class JCodeDeptDataPermissionAutoConfiguration {

    @Bean
    public DeptDataPermissionRule deptDataPermissionRule(PermissionCommonApi permissionApi, List<DeptDataPermissionRuleCustomizer> customizers) {
        // Cloud 专属逻辑：优先使用本地的 PermissionApi 实现类，而不是 Feign 调用
        // 原因：在创建租户时，租户还没创建好，导致 Feign 调用获取数据权限时，报“租户不存在”的错误
        try {
            PermissionCommonApi permissionApiImpl = SpringUtil.getBean("permissionApiImpl", PermissionCommonApi.class);
            if (permissionApiImpl != null) {
                permissionApi = permissionApiImpl;
            }
        } catch (Exception ignored) {
            // ignored
        }

        // 创建 DeptDataPermissionRule 对象
        DeptDataPermissionRule rule = new DeptDataPermissionRule(permissionApi);
        // 补全表配置
        customizers.forEach(customizer -> customizer.customize(rule));
        return rule;
    }
}
