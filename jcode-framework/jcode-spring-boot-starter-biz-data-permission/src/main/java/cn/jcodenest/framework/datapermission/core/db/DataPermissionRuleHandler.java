package cn.jcodenest.framework.datapermission.core.db;

import cn.hutool.core.collection.CollUtil;
import cn.jcodenest.framework.datapermission.core.rule.DataPermissionRule;
import cn.jcodenest.framework.datapermission.core.rule.DataPermissionRuleFactory;
import cn.jcodenest.framework.mybatis.core.util.MyBatisUtils;
import cn.jcodenest.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Table;

import java.util.List;

/**
 * 基于 {@link DataPermissionRule} 的数据权限处理器
 *
 * <p>
 * 它的底层是基于 MyBatis Plus 的 <a href="https://baomidou.com/plugins/data-permission/">数据权限插件</a>
 * 核心原理：它会在 SQL 执行前拦截 SQL 语句，并根据用户权限动态添加权限相关的 SQL 片段, 这样只有用户有权限访问的数据才会被查询出来
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@RequiredArgsConstructor
public class DataPermissionRuleHandler implements MultiDataPermissionHandler {

    private final DataPermissionRuleFactory ruleFactory;

    /**
     * 获得 SQL 片段
     *
     * @param table           表名
     * @param where           WHERE 条件
     * @param mappedStatementId Mapper 方法的编号
     * @return SQL 片段
     */
    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        // 特殊：跨租户访问
        if (SecurityFrameworkUtils.skipPermissionCheck()) {
            return null;
        }

        // 获得 Mapper 对应的数据权限的规则
        List<DataPermissionRule> rules = ruleFactory.getDataPermissionRule(mappedStatementId);
        if (CollUtil.isEmpty(rules)) {
            return null;
        }

        // 生成条件
        Expression allExpression = null;
        String tableName = MyBatisUtils.getTableName(table);
        for (DataPermissionRule rule : rules) {
            // 跳过不匹配的表名或无有效表达式的规则
            if (!rule.getTableNames().contains(tableName) || rule.getExpression(tableName, table.getAlias()) == null) {
                continue;
            }

            // 单条规则的条件
            Expression oneExpress = rule.getExpression(tableName, table.getAlias());
            // 拼接到 allExpression 中
            allExpression = allExpression == null ? oneExpress : new AndExpression(allExpression, oneExpress);
        }
        return allExpression;
    }
}
