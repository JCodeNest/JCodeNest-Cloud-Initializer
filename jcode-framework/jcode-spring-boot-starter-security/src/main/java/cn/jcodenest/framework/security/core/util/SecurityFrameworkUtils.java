package cn.jcodenest.framework.security.core.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.security.core.LoginUser;
import cn.jcodenest.framework.web.core.util.WebFrameworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;

import java.util.Collections;

/**
 * 安全服务工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityFrameworkUtils {

    /**
     * HEADER 认证头 value 的前缀
     */
    public static final String AUTHORIZATION_BEARER = "Bearer";

    /**
     * HEADER 认证头用户 key
     */
    public static final String LOGIN_USER_HEADER = "login-user";

    /**
     * 从请求中获取认证 Token
     *
     * @param request       请求
     * @param headerName    认证 Token 对应的 Header 名字
     * @param parameterName 认证 Token 对应的 Parameter 名字
     * @return 认证 Token
     */
    public static String obtainAuthorization(HttpServletRequest request, String headerName, String parameterName) {
        // 优先级：Header > Parameter
        String token = request.getHeader(headerName);
        if (StrUtil.isEmpty(token)) {
            token = request.getParameter(parameterName);
        }

        if (!StringUtils.hasText(token)) {
            return null;
        }

        // 去除 Token 中带的 Bearer
        int index = token.indexOf(AUTHORIZATION_BEARER + " ");
        return index >= 0 ? token.substring(index + 7).trim() : token;
    }

    /**
     * 获取当前认证信息
     *
     * @return 认证信息
     */
    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }

        return context.getAuthentication();
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户
     */
    @Nullable
    public static LoginUser getLoginUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }

        return authentication.getPrincipal() instanceof LoginUser ? (LoginUser) authentication.getPrincipal() : null;
    }

    /**
     * 从上下文中获取当前用户的编号
     *
     * @return 用户编号
     */
    @Nullable
    public static Long getLoginUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getId() : null;
    }

    /**
     * 从上下文中获取当前用户的昵称
     *
     * @return 昵称
     */
    @Nullable
    public static String getLoginUserNickname() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? MapUtil.getStr(loginUser.getInfo(), LoginUser.INFO_KEY_NICKNAME) : null;
    }

    /**
     * 从上下文中获取当前用户的部门编号
     *
     * @return 部门编号
     */
    @Nullable
    public static Long getLoginUserDeptId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? MapUtil.getLong(loginUser.getInfo(), LoginUser.INFO_KEY_DEPT_ID) : null;
    }

    /**
     * 设置当前用户
     *
     * @param loginUser 登录用户
     * @param request   请求
     */
    public static void setLoginUser(LoginUser loginUser, HttpServletRequest request) {
        // 创建 Authentication, 并设置到上下文
        Authentication authentication = buildAuthentication(loginUser, request);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 额外设置到 request 中用于 ApiAccessLogFilter 可以获取到用户编号
        // 原因是 Spring Security 的 Filter 在 ApiAccessLogFilter 后面, 在它记录访问日志时线上上下文已经没有用户编号等信息
        if (request != null) {
            WebFrameworkUtils.setLoginUserId(request, loginUser.getId());
            WebFrameworkUtils.setLoginUserType(request, loginUser.getUserType());
        }
    }

    /**
     * 创建 Authentication
     *
     * @param loginUser 登录用户
     * @param request   请求
     * @return Authentication
     */
    private static Authentication buildAuthentication(LoginUser loginUser, HttpServletRequest request) {
        // 创建 UsernamePasswordAuthenticationToken 对象
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;
    }

    /**
     * 是否条件跳过权限校验, 包括数据权限、功能权限
     *
     * @return 是否跳过
     */
    public static boolean skipPermissionCheck() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return false;
        }

        if (loginUser.getVisitTenantId() == null) {
            return false;
        }

        // 重点：跨租户访问时, 无法进行权限校验
        return ObjUtil.notEqual(loginUser.getVisitTenantId(), loginUser.getTenantId());
    }

}
