package cn.jcodenest.framework.datapermission.core.rule.dept;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.common.biz.system.permission.PermissionCommonApi;
import cn.jcodenest.framework.common.biz.system.permission.dto.DeptDataPermissionRespDTO;
import cn.jcodenest.framework.common.enums.UserTypeEnum;
import cn.jcodenest.framework.common.util.collection.CollectionUtils;
import cn.jcodenest.framework.common.util.json.JsonUtils;
import cn.jcodenest.framework.datapermission.core.rule.DataPermissionRule;
import cn.jcodenest.framework.mybatis.core.dataobject.BaseDO;
import cn.jcodenest.framework.mybatis.core.util.MyBatisUtils;
import cn.jcodenest.framework.security.core.LoginUser;
import cn.jcodenest.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 基于部门的 {@link DataPermissionRule} 数据权限规则实现
 *
 * <p>
 *     注意: 使用 DeptDataPermissionRule 时，需要保证表中有 dept_id 部门编号的字段，可自定义。
 *     实际业务场景下，会存在一个经典的问题: 当用户修改部门时，冗余的 dept_id 是否需要修改？
 *     1. 一般情况下，dept_id 不进行修改，则会导致用户看不到之前的数据。【jcode-server 采用该方案】
 *     2. 部分情况下，希望该用户还是能看到之前的数据，则有两种方式解决：【需要你改造该 DeptDataPermissionRule 的实现代码】
 *      2.1 编写洗数据的脚本，将 dept_id 修改成新部门的编号；【建议】
 *          最终过滤条件是 WHERE dept_id = ?
 *      2.2 洗数据的话，可能涉及的数据量较大，也可以采用 user_id 进行过滤的方式，此时需要获取到 dept_id 对应的所有 user_id 用户编号；
 *          最终过滤条件是 WHERE user_id IN (?, ?, ? ...)
 *      2.3 想要保证原 dept_id 和 user_id 都可以看的到，此时使用 dept_id 和 user_id 一起过滤；
 *          最终过滤条件是 WHERE dept_id = ? OR user_id IN (?, ?, ? ...)
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@AllArgsConstructor
public class DeptDataPermissionRule implements DataPermissionRule {

    /**
     * LoginUser 的 Context 缓存 Key
     */
    protected static final String CONTEXT_KEY = DeptDataPermissionRule.class.getSimpleName();

    /**
     * 部门编号字段的默认值
     */
    private static final String DEPT_COLUMN_NAME = "dept_id";

    /**
     * 用户编号字段的默认值
     */
    private static final String USER_COLUMN_NAME = "user_id";

    /**
     * 表示 NULL 的表达式
     */
    static final Expression EXPRESSION_NULL = new NullValue();

    /**
     * 登录用户的权限 API
     */
    private final PermissionCommonApi permissionApi;

    /**
     * 基于部门的表字段配置
     * 一般情况下，每个表的部门编号字段是 dept_id，通过该配置自定义。
     * <p>
     * key：表名
     * value：字段名
     */
    private final Map<String, String> deptColumns = new HashMap<>();
    /**
     * 基于用户的表字段配置
     * 一般情况下，每个表的部门编号字段是 dept_id，通过该配置自定义。
     * <p>
     * key：表名
     * value：字段名
     */
    private final Map<String, String> userColumns = new HashMap<>();

    /**
     * 所有表名，是 {@link #deptColumns} 和 {@link #userColumns} 的合集
     */
    private final Set<String> TABLE_NAMES = new HashSet<>();

    /**
     * 问题：返回需要生效的表名数组?
     * 回答：Data Permission 数组基于 SQL 重写，通过 Where 返回只有权限的数据
     * <p>
     * 如果需要基于实体名获得表名，可调用 {@link TableInfoHelper#getTableInfo(Class)} 获得
     *
     * @return 表名数组
     */
    @Override
    public Set<String> getTableNames() {
        return TABLE_NAMES;
    }

    /**
     * 根据表名和别名，生成对应的 WHERE / OR 过滤条件
     *
     * @param tableName  表名
     * @param tableAlias 别名，可能为空
     * @return 过滤条件 Expression 表达式
     */
    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        // 只有有登陆用户的情况下，才进行数据权限的处理
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return null;
        }

        // 只有管理员类型的用户，才进行数据权限的处理
        if (ObjectUtil.notEqual(loginUser.getUserType(), UserTypeEnum.ADMIN.getValue())) {
            return null;
        }

        // 获得数据权限
        DeptDataPermissionRespDTO deptDataPermission = loginUser.getContext(CONTEXT_KEY, DeptDataPermissionRespDTO.class);

        // 从上下文中拿不到，则调用逻辑进行获取
        if (deptDataPermission == null) {
            deptDataPermission = permissionApi.getDeptDataPermission(loginUser.getId()).getCheckedData();
            if (deptDataPermission == null) {
                log.error("[getExpression][LoginUser({}) 获取数据权限为 null]", JsonUtils.toJsonString(loginUser));
                throw new NullPointerException(String.format("LoginUser(%d) Table(%s/%s) 未返回数据权限", loginUser.getId(), tableName, tableAlias.getName()));
            }

            // 添加到上下文中，避免重复计算
            loginUser.setContext(CONTEXT_KEY, deptDataPermission);
        }

        // 情况一: 如果是 ALL 可查看全部，则无需拼接条件
        if (Boolean.TRUE.equals(deptDataPermission.getAll())) {
            return null;
        }

        // 情况二: 即不能查看部门，又不能查看自己，则说明 100% 无权限
        if (CollUtil.isEmpty(deptDataPermission.getDeptIds()) && Boolean.FALSE.equals(deptDataPermission.getSelf())) {
            // WHERE null = null，可以保证返回的数据为空
            return new EqualsTo(null, null);
        }

        // 情况三: 拼接 Dept 和 User 的条件，最后组合
        Expression deptExpression = buildDeptExpression(tableName, tableAlias, deptDataPermission.getDeptIds());
        Expression userExpression = buildUserExpression(tableName, tableAlias, deptDataPermission.getSelf(), loginUser.getId());
        if (deptExpression == null && userExpression == null) {
            // TODO：获得不到条件的时候，暂时不抛出异常，而是不返回数据
            log.warn("[getExpression][LoginUser({}) Table({}/{}) DeptDataPermission({}) 构建的条件为空]",
                    JsonUtils.toJsonString(loginUser), tableName, tableAlias, JsonUtils.toJsonString(deptDataPermission));
            //throw new NullPointerException(String.format("LoginUser(%d) Table(%s/%s) 构建的条件为空",
            //        loginUser.getId(), tableName, tableAlias.getName()));
            return EXPRESSION_NULL;
        }

        if (deptExpression == null) {
            return userExpression;
        }

        if (userExpression == null) {
            return deptExpression;
        }

        // 目前如果有指定部门 + 可查看自己，采用 OR 条件。即 WHERE (dept_id IN ? OR user_id = ?)
        return new ParenthesedExpressionList<>(new OrExpression(deptExpression, userExpression));
    }

    /**
     * 构建部门条件
     *
     * @param tableName  表名
     * @param tableAlias 别名
     * @param deptIds    部门编号数组
     * @return 部门条件
     */
    private Expression buildDeptExpression(String tableName, Alias tableAlias, Set<Long> deptIds) {
        // 如果不存在配置，则无需作为条件
        String columnName = deptColumns.get(tableName);
        if (StrUtil.isEmpty(columnName)) {
            return null;
        }

        // 如果为空，则无条件
        if (CollUtil.isEmpty(deptIds)) {
            return null;
        }

        // 拼接条件
        return new InExpression(MyBatisUtils.buildColumn(tableName, tableAlias, columnName),
                // Parenthesis 的目的，是提供 (1,2,3) 的 () 左右括号
                new ParenthesedExpressionList<>(new ExpressionList<LongValue>(CollectionUtils.convertList(deptIds, LongValue::new))));
    }

    /**
     * 构建用户条件
     *
     * @param tableName  表名
     * @param tableAlias 别名
     * @param self 是否查看自己
     * @param userId 用户编号
     * @return 用户条件
     */
    private Expression buildUserExpression(String tableName, Alias tableAlias, Boolean self, Long userId) {
        // 如果不查看自己，则无需作为条件
        if (Boolean.FALSE.equals(self)) {
            return null;
        }

        String columnName = userColumns.get(tableName);
        if (StrUtil.isEmpty(columnName)) {
            return null;
        }

        // 拼接条件
        return new EqualsTo(MyBatisUtils.buildColumn(tableName, tableAlias, columnName), new LongValue(userId));
    }

    // ==================== 添加配置 ====================

    /**
     * 添加部门字段
     *
     * @param entityClass 实体类
     */
    public void addDeptColumn(Class<? extends BaseDO> entityClass) {
        addDeptColumn(entityClass, DEPT_COLUMN_NAME);
    }

    /**
     * 添加部门字段
     *
     * @param entityClass 实体类
     * @param columnName 字段名
     */
    public void addDeptColumn(Class<? extends BaseDO> entityClass, String columnName) {
        String tableName = TableInfoHelper.getTableInfo(entityClass).getTableName();
        addDeptColumn(tableName, columnName);
    }

    /**
     * 添加部门字段
     *
     * @param tableName 表名
     * @param columnName 字段名
     */
    public void addDeptColumn(String tableName, String columnName) {
        deptColumns.put(tableName, columnName);
        TABLE_NAMES.add(tableName);
    }

    /**
     * 添加用户字段
     *
     * @param entityClass 实体类
     */
    public void addUserColumn(Class<? extends BaseDO> entityClass) {
        addUserColumn(entityClass, USER_COLUMN_NAME);
    }

    /**
     * 添加用户字段
     *
     * @param entityClass 实体类
     * @param columnName 字段名
     */
    public void addUserColumn(Class<? extends BaseDO> entityClass, String columnName) {
        String tableName = TableInfoHelper.getTableInfo(entityClass).getTableName();
        addUserColumn(tableName, columnName);
    }

    /**
     * 添加用户字段
     *
     * @param tableName 表名
     * @param columnName 字段名
     */
    public void addUserColumn(String tableName, String columnName) {
        userColumns.put(tableName, columnName);
        TABLE_NAMES.add(tableName);
    }
}
