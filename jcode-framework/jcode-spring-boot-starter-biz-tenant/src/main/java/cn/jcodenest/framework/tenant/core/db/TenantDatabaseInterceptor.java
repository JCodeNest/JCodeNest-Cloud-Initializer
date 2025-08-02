package cn.jcodenest.framework.tenant.core.db;

import cn.jcodenest.framework.tenant.config.properties.TenantProperties;
import cn.jcodenest.framework.tenant.core.aop.TenantIgnore;
import cn.jcodenest.framework.tenant.core.content.TenantContextHolder;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于 MyBatis Plus 多租户的功能，实现 DB 层面的多租户的功能
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class TenantDatabaseInterceptor implements TenantLineHandler {

    /**
     * 忽略的表
     * <p>
     * KEY：表名
     * VALUE：是否忽略
     */
    private final Map<String, Boolean> ignoreTables = new HashMap<>();

    /**
     * 构造器
     *
     * @param properties 配置
     */
    public TenantDatabaseInterceptor(TenantProperties properties) {
        // 不同 DB 下大小写的习惯不同，所以需要都添加进去
        properties.getIgnoreTables().forEach(table -> addIgnoreTable(table, true));
        // 在 OracleKeyGenerator 中生成主键时会查询这个表，查询这个表后会自动拼接 TENANT_ID 导致报错
        addIgnoreTable("DUAL", true);
    }

    /**
     * 获取租户的 SQL
     *
     * @return  SQL
     */
    @Override
    public Expression getTenantId() {
        return new LongValue(TenantContextHolder.getRequiredTenantId());
    }

    /**
     * 是否忽略表名
     *
     * @param tableName 表名
     * @return  是否忽略
     */
    @Override
    public boolean ignoreTable(String tableName) {
        // 情况一：全局忽略多租户
        if (TenantContextHolder.isIgnore()) {
            return true;
        }

        // 情况二：忽略多租户的表
        tableName = SqlParserUtils.removeWrapperSymbol(tableName);
        Boolean ignore = ignoreTables.get(tableName.toLowerCase());
        if (ignore == null) {
            ignore = computeIgnoreTable(tableName);
            synchronized (ignoreTables) {
                addIgnoreTable(tableName, ignore);
            }
        }

        return ignore;
    }

    /**
     * 添加忽略的表
     *
     * @param tableName 表名
     * @param ignore 是否忽略
     */
    private void addIgnoreTable(String tableName, boolean ignore) {
        ignoreTables.put(tableName.toLowerCase(), ignore);
        ignoreTables.put(tableName.toUpperCase(), ignore);
    }

    /**
     * 计算是否忽略某个表
     *
     * @param tableName 表名
     * @return 是否忽略
     */
    private boolean computeIgnoreTable(String tableName) {
        // 找不到的表，说明不是 jcode 项目里的，不进行拦截（忽略租户）
        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
        if (tableInfo == null) {
            return true;
        }

        // 如果继承了 TenantBaseDO 基类，显然不忽略租户
        if (TenantBaseDO.class.isAssignableFrom(tableInfo.getEntityType())) {
            return false;
        }

        // 如果添加了 @TenantIgnore 注解，显然也不忽略租户
        TenantIgnore tenantIgnore = tableInfo.getEntityType().getAnnotation(TenantIgnore.class);
        return tenantIgnore != null;
    }
}
