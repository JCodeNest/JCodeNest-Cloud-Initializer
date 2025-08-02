package cn.jcodenest.framework.tenant.core.web;

import cn.hutool.core.util.ObjUtil;
import cn.jcodenest.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.jcodenest.framework.security.core.LoginUser;
import cn.jcodenest.framework.security.core.service.SecurityFrameworkService;
import cn.jcodenest.framework.security.core.util.SecurityFrameworkUtils;
import cn.jcodenest.framework.tenant.config.properties.TenantProperties;
import cn.jcodenest.framework.tenant.core.content.TenantContextHolder;
import cn.jcodenest.framework.web.core.util.WebFrameworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static cn.jcodenest.framework.common.util.exception.ServiceExceptionUtil.exception0;

/**
 * 跨越租户访问的拦截器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@RequiredArgsConstructor
public class TenantVisitContextInterceptor implements HandlerInterceptor {

    /**
     * 多租户的权限 key
     */
    private static final String PERMISSION = "system:tenant:visit";

    /**
     * 多租户属性类
     */
    private final TenantProperties tenantProperties;

    /**
     * Security 框架 Service 接口
     */
    private final SecurityFrameworkService securityFrameworkService;

    /**
     * 在请求前拦截，验证用户是否有访问多租户的权限
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return true if the execution chain should proceed with the next interceptor or the handler itself.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果和当前租户编号一致，则直接跳过
        Long visitTenantId = WebFrameworkUtils.getVisitTenantId(request);
        if (visitTenantId == null) {
            return true;
        }
        if (ObjUtil.equal(visitTenantId, TenantContextHolder.getTenantId())) {
            return true;
        }

        // 必须是登录用户
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return true;
        }

        // 校验用户是否可切换租户
        if (!securityFrameworkService.hasAnyPermissions(PERMISSION)) {
            throw exception0(GlobalErrorCodeConstants.FORBIDDEN.getCode(), "您无权切换租户");
        }

        // 【重点】切换租户编号
        loginUser.setVisitTenantId(visitTenantId);
        TenantContextHolder.setTenantId(visitTenantId);
        return true;
    }

    /**
     * 在请求结束后，清理租户编号
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the handler (or {@link HandlerMethod}) that started asynchronous
     * execution, for type and/or instance examination
     * @param ex any exception thrown on handler execution, if any; this does not
     * include exceptions that have been handled through an exception resolver
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 【重点】清理切换，换回原租户编号
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser != null && loginUser.getTenantId() != null) {
            TenantContextHolder.setTenantId(loginUser.getTenantId());
        }
    }
}
