package cn.jcodenest.framework.datapermission.core.rule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.jcodenest.framework.datapermission.core.annotation.DataPermission;
import cn.jcodenest.framework.datapermission.core.aop.DataPermissionContextHolder;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认的 {@link DataPermissionRuleFactory} 实现类, 支持通过 {@link DataPermissionContextHolder} 过滤数据权限
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@RequiredArgsConstructor
public class DataPermissionRuleFactoryImpl implements DataPermissionRuleFactory {

    /**
     * 数据权限规则数组
     */
    private final List<DataPermissionRule> rules;

    /**
     * 获得所有数据权限规则数组
     *
     * @return 数据权限规则数组
     */
    @Override
    public List<DataPermissionRule> getDataPermissionRules() {
        return rules;
    }

    /**
     * 获得指定 Mapper 的数据权限规则数组
     * mappedStatementId 参数暂时没有用, 以后可以基于 mappedStatementId + DataPermission 进行缓存
     *
     * @param mappedStatementId 指定 Mapper 的编号
     * @return 数据权限规则数组
     */
    @Override
    public List<DataPermissionRule> getDataPermissionRule(String mappedStatementId) {
        // 无数据权限
        if (CollUtil.isEmpty(rules)) {
            return Collections.emptyList();
        }

        // 未配置，则默认开启
        DataPermission dataPermission = DataPermissionContextHolder.get();
        if (dataPermission == null) {
            return rules;
        }

        // 已配置，但禁用
        if (!dataPermission.enable()) {
            return Collections.emptyList();
        }

        // 已配置，只选择部分规则
        if (ArrayUtil.isNotEmpty(dataPermission.includeRules())) {
            // 一般规则不会太多，所以不采用 HashSet 查询
            return rules.stream()
                    .filter(rule -> ArrayUtil.contains(dataPermission.includeRules(), rule.getClass()))
                    .collect(Collectors.toList());
        }

        // 已配置，只排除部分规则
        if (ArrayUtil.isNotEmpty(dataPermission.excludeRules())) {
            // 一般规则不会太多，所以不采用 HashSet 查询
            return rules.stream()
                    .filter(rule -> !ArrayUtil.contains(dataPermission.excludeRules(), rule.getClass()))
                    .collect(Collectors.toList());
        }

        // 已配置，全部规则
        return rules;
    }
}
