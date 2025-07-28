package cn.jcodenest.framework.web.core.util;

import cn.hutool.core.util.NumberUtil;
import cn.jcodenest.framework.common.constants.RpcConstants;
import cn.jcodenest.framework.common.enums.TerminalEnum;
import cn.jcodenest.framework.common.enums.UserTypeEnum;
import cn.jcodenest.framework.common.pojo.CommonResult;
import cn.jcodenest.framework.web.config.WebProperties;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Web 包专用工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/28
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class WebFrameworkUtils {

    /**
     * 当前登录用户ID
     */
    private static final String REQUEST_ATTRIBUTE_LOGIN_USER_ID = "login_user_id";

    /**
     * 当前登录用户类型
     */
    private static final String REQUEST_ATTRIBUTE_LOGIN_USER_TYPE = "login_user_type";

    /**
     * 通用返回对象
     */
    private static final String REQUEST_ATTRIBUTE_COMMON_RESULT = "common_result";

    /**
     * 租户编号
     */
    public static final String HEADER_TENANT_ID = "tenant-id";

    /**
     * 访问租户编号
     */
    public static final String HEADER_VISIT_TENANT_ID = "visit-tenant-id";

    /**
     * 终端 Header
     *
     * @see cn.jcodenest.framework.common.enums.TerminalEnum
     */
    public static final String HEADER_TERMINAL = "terminal";

    /**
     * 自定义 Web 属性配置类
     */
    private static WebProperties properties;

    /**
     * 初始化
     *
     * @param webProperties 自定义 Web 属性配置类
     */
    public WebFrameworkUtils(WebProperties webProperties) {
        WebFrameworkUtils.properties = webProperties;
    }

    /**
     * 从请求中获取租户编号
     *
     * @param request 请求
     * @return 租户编号
     */
    public static Long getTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader(HEADER_TENANT_ID);
        return NumberUtil.isNumber(tenantId) ? Long.valueOf(tenantId) : null;
    }

    /**
     * 从请求中获取访问租户编号
     *
     * @param request 请求
     * @return 访问租户编号
     */
    public static Long getVisitTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader(HEADER_VISIT_TENANT_ID);
        return NumberUtil.isNumber(tenantId) ? Long.valueOf(tenantId) : null;
    }

    /**
     * 从请求中获取当前登录用户编号
     *
     * <p>注意: 该方法仅限于 framework 框架使用！</p>
     *
     * @param request 请求
     * @return 当前登录用户编号
     */
    public static Long getLoginUserId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        return (Long) request.getAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID);
    }

    /**
     * 从请求中获取当前登录用户编号
     *
     * @return 当前登录用户编号
     */
    public static Long getLoginUserId() {
        HttpServletRequest request = getRequest();
        return getLoginUserId(request);
    }

    /**
     * 设置当前登录用户编号
     *
     * @param request 请求
     * @param userId  用户编号
     */
    public static void setLoginUserId(ServletRequest request, Long userId) {
        request.setAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID, userId);
    }

    /**
     * 从请求中获取当前登录用户类型
     *
     * @param request 请求
     * @return 当前登录用户类型
     */
    public static Integer getLoginUserType(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        // 1. 优先从 Attribute 中获取
        Integer userType = (Integer) request.getAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_TYPE);
        if (userType != null) {
            return userType;
        }

        // 2. 其次基于 URL 前缀的约定
        if (request.getServletPath().startsWith(properties.getAdminApi().getPrefix())) {
            return UserTypeEnum.ADMIN.getValue();
        }
        if (request.getServletPath().startsWith(properties.getAppApi().getPrefix())) {
            return UserTypeEnum.MEMBER.getValue();
        }

        return null;
    }

    /**
     * 从请求中获取当前登录用户类型
     *
     * @return 当前登录用户类型
     */
    public static Integer getLoginUserType() {
        HttpServletRequest request = getRequest();
        return getLoginUserType(request);
    }

    /**
     * 设置当前登录用户类型
     *
     * @param request  请求
     * @param userType 用户类型
     */
    public static void setLoginUserType(ServletRequest request, Integer userType) {
        request.setAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_TYPE, userType);
    }

    /**
     * 从请求中获取终端类型
     *
     * @return 终端类型
     */
    public static Integer getTerminal() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return TerminalEnum.UNKNOWN.getTerminal();
        }

        String terminalValue = request.getHeader(HEADER_TERMINAL);
        return NumberUtil.parseInt(terminalValue, TerminalEnum.UNKNOWN.getTerminal());
    }

    /**
     * 从请求中获取通用返回对象
     *
     * @param request 请求
     * @return 通用返回对象
     */
    public static CommonResult<?> getCommonResult(ServletRequest request) {
        return (CommonResult<?>) request.getAttribute(REQUEST_ATTRIBUTE_COMMON_RESULT);
    }

    /**
     * 设置通用返回对象
     *
     * @param request 请求
     * @param result  通用返回对象
     */
    public static void setCommonResult(ServletRequest request, CommonResult<?> result) {
        request.setAttribute(REQUEST_ATTRIBUTE_COMMON_RESULT, result);
    }

    /**
     * 获取 HttpServletRequest
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }

        return servletRequestAttributes.getRequest();
    }

    /**
     * 是否为 RPC 请求
     *
     * @param request 请求
     * @return 是否为 RPC 请求
     */
    public static boolean isRpcRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith(RpcConstants.RPC_API_PREFIX);
    }

    /**
     * 是否为 RPC 请求
     *
     * @param className 类名
     * @return 是否为 RPC 请求
     */
    public static boolean isRpcRequest(String className) {
        return className.endsWith("Api");
    }
}
