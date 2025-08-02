package cn.jcodenest.framework.tenant.core.security;

import cn.hutool.core.collection.CollUtil;
import cn.jcodenest.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.jcodenest.framework.common.pojo.CommonResult;
import cn.jcodenest.framework.common.util.servlet.ServletUtils;
import cn.jcodenest.framework.security.core.LoginUser;
import cn.jcodenest.framework.security.core.util.SecurityFrameworkUtils;
import cn.jcodenest.framework.tenant.config.properties.TenantProperties;
import cn.jcodenest.framework.tenant.core.content.TenantContextHolder;
import cn.jcodenest.framework.tenant.core.service.TenantFrameworkService;
import cn.jcodenest.framework.web.config.WebProperties;
import cn.jcodenest.framework.web.core.filter.ApiRequestFilter;
import cn.jcodenest.framework.web.core.handler.GlobalExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Objects;

/**
 * 多租户 Security Web 过滤器
 * <p>
 * 1. 如果是登陆的用户，校验是否有权限访问该租户，避免越权问题。
 * 2. 如果请求未带租户的编号，检查是否是忽略的 URL，否则也不允许访问。
 * 3. 校验租户是合法，例如说被禁用、到期。
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
public class TenantSecurityWebFilter extends ApiRequestFilter {

    /**
     * 租户配置类
     */
    private final TenantProperties tenantProperties;

    /**
     * 路径匹配器
     */
    private final AntPathMatcher pathMatcher;

    /**
     * 全局异常处理器
     */
    private final GlobalExceptionHandler globalExceptionHandler;

    /**
     * 租户服务
     */
    private final TenantFrameworkService tenantFrameworkService;

    /**
     * 构造器
     *
     * @param tenantProperties        租户配置类
     * @param webProperties           web 配置类
     * @param globalExceptionHandler  全局异常处理器
     * @param tenantFrameworkService  租户服务
     */
    public TenantSecurityWebFilter(TenantProperties tenantProperties,
                                   WebProperties webProperties,
                                   GlobalExceptionHandler globalExceptionHandler,
                                   TenantFrameworkService tenantFrameworkService) {
        super(webProperties);
        this.tenantProperties = tenantProperties;
        this.pathMatcher = new AntPathMatcher();
        this.globalExceptionHandler = globalExceptionHandler;
        this.tenantFrameworkService = tenantFrameworkService;
    }

    /**
     * 过滤请求
     *
     * @param request 请求
     * @param response 响应
     * @param chain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Long tenantId = TenantContextHolder.getTenantId();

        // 登陆的用户，校验是否有权限访问该租户，避免越权问题
        LoginUser user = SecurityFrameworkUtils.getLoginUser();
        if (user != null) {
            // 如果获取不到租户编号，则尝试使用登陆用户的租户编号
            if (tenantId == null) {
                tenantId = user.getTenantId();
                TenantContextHolder.setTenantId(tenantId);
                // 如果传递了租户编号，则进行比对租户编号，避免越权问题
            } else if (!Objects.equals(user.getTenantId(), TenantContextHolder.getTenantId())) {
                log.error("[doFilterInternal][租户({}) User({}/{}) 越权访问租户({}) URL({}/{})]",
                        user.getTenantId(), user.getId(), user.getUserType(),
                        TenantContextHolder.getTenantId(), request.getRequestURI(), request.getMethod());
                ServletUtils.writeJSON(response, CommonResult.error(GlobalErrorCodeConstants.FORBIDDEN.getCode(), "您无权访问该租户的数据"));
                return;
            }
        }

        // 如果非允许忽略租户的 URL，则校验租户是否合法
        if (!isIgnoreUrl(request)) {
            // 如果请求未带租户的编号，不允许访问
            if (tenantId == null) {
                log.error("[doFilterInternal][URL({}/{}) 未传递租户编号]", request.getRequestURI(), request.getMethod());
                ServletUtils.writeJSON(response, CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "请求的租户标识未传递，请进行排查"));
                return;
            }

            // 校验租户是合法，例如被禁用、到期
            try {
                tenantFrameworkService.validTenant(tenantId);
            } catch (Throwable ex) {
                CommonResult<?> result = globalExceptionHandler.allExceptionHandler(request, ex);
                ServletUtils.writeJSON(response, result);
                return;
            }
        } else {
            // 如果是允许忽略租户的 URL，若未传递租户编号，则默认忽略租户编号，避免报错
            if (tenantId == null) {
                TenantContextHolder.setIgnore(true);
            }
        }

        // 继续过滤
        chain.doFilter(request, response);
    }

    /**
     * 判断是否忽略
     *
     * @param request 请求
     * @return 是否忽略
     */
    private boolean isIgnoreUrl(HttpServletRequest request) {
        // 快速匹配，保证性能
        if (CollUtil.contains(tenantProperties.getIgnoreUrls(), request.getRequestURI())) {
            return true;
        }

        // 逐个 Ant 路径匹配
        for (String url : tenantProperties.getIgnoreUrls()) {
            if (pathMatcher.match(url, request.getRequestURI())) {
                return true;
            }
        }

        return false;
    }
}
