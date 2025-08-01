package cn.jcodenest.framework.security.core;

import cn.hutool.core.map.MapUtil;
import cn.jcodenest.framework.common.enums.UserTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录用户信息
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@Accessors(chain = true)
public class LoginUser {

    /**
     * 用户呢称
     */
    public static final String INFO_KEY_NICKNAME = "nickname";

    /**
     * 部门ID
     */
    public static final String INFO_KEY_DEPT_ID = "deptId";

    /**
     * 用户编号
     */
    private Long id;

    /**
     * 用户类型
     * <p>
     * 关联 {@link UserTypeEnum}
     */
    private Integer userType;

    /**
     * 额外的用户信息
     */
    private Map<String, String> info;

    /**
     * 租户编号
     */
    private Long tenantId;

    /**
     * 授权范围
     */
    private List<String> scopes;

    /**
     * 过期时间
     */
    private LocalDateTime expiresTime;

    // ========== 上下文 ==========

    /**
     * 上下文字段不进行持久化, 用于基于 LoginUser 维度的临时缓存
     */
    @JsonIgnore
    private Map<String, Object> context;

    /**
     * 访问的租户编号
     */
    private Long visitTenantId;

    /**
     * 设置上下文
     *
     * @param key   键
     * @param value 值
     */
    public void setContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
    }

    /**
     * 获取上下文
     *
     * @param key  键
     * @param type 类型
     * @param <T>  类型
     * @return 值
     */
    public <T> T getContext(String key, Class<T> type) {
        return MapUtil.get(context, key, type);
    }
}
