package cn.jcodenest.framework.security.core.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import cn.jcodenest.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import cn.jcodenest.framework.common.exception.ServiceException;
import cn.jcodenest.framework.common.pojo.CommonResult;
import cn.jcodenest.framework.common.util.json.JsonUtils;
import cn.jcodenest.framework.common.util.servlet.ServletUtils;
import cn.jcodenest.framework.security.config.properties.SecurityProperties;
import cn.jcodenest.framework.security.core.LoginUser;
import cn.jcodenest.framework.security.core.util.SecurityFrameworkUtils;
import cn.jcodenest.framework.web.core.handler.GlobalExceptionHandler;
import cn.jcodenest.framework.web.core.util.WebFrameworkUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Token 过滤器, 验证 token 的有效性
 *
 * <p>验证通过后, 获取 {@link LoginUser} 信息， 并加入到 Spring Security 上下文</p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;
    private final GlobalExceptionHandler globalExceptionHandler;
    private final OAuth2TokenCommonApi oauth2TokenApi;

    /**
     * 过滤器逻辑
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException 服务端异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 情况一: 基于 header[login-user] 获得用户, 例如来自 Gateway 或者其它服务透传
        LoginUser loginUser = buildLoginUserByHeader(request);

        // 情况二: 基于 Token 获得用户
        // 注意: 这里主要满足直接使用 Nginx 直接转发到 Spring Cloud 服务的场景
        if (loginUser == null) {
            String token = SecurityFrameworkUtils.obtainAuthorization(request, securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
            if (StrUtil.isNotEmpty(token)) {
                Integer userType = WebFrameworkUtils.getLoginUserType(request);

                try {
                    // 1.1 基于 token 构建登录用户
                    loginUser = buildLoginUserByToken(token, userType);

                    // 1.2 模拟 Login 功能, 方便日常开发调试
                    if (loginUser == null) {
                        loginUser = mockLoginUser(request, token, userType);
                    }
                } catch (Throwable ex) {
                    CommonResult<?> result = globalExceptionHandler.allExceptionHandler(request, ex);
                    ServletUtils.writeJSON(response, result);
                    return;
                }
            }
        }

        // 设置当前用户
        if (loginUser != null) {
            SecurityFrameworkUtils.setLoginUser(loginUser, request);
        }

        // 继续过滤链
        filterChain.doFilter(request, response);
    }

    /**
     * 通过 token 构建 LoginUser
     *
     * @param token    token
     * @param userType 用户类型
     * @return LoginUser
     */
    private LoginUser buildLoginUserByToken(String token, Integer userType) {
        try {
            // 校验访问令牌
            OAuth2AccessTokenCheckRespDTO accessToken = oauth2TokenApi.checkAccessToken(token).getCheckedData();
            if (accessToken == null) {
                return null;
            }

            // 用户类型不匹配, 无权限
            // 注意：只有 /admin-api/* 和 /app-api/* 有 userType, 才需要比对用户类型. 类似 WebSocket 的 /ws/* 连接地址, 不需要比对用户类型
            if (userType != null && ObjectUtil.notEqual(accessToken.getUserType(), userType)) {
                throw new AccessDeniedException("错误的用户类型");
            }

            // 构建登录用户
            return new LoginUser()
                    .setId(accessToken.getUserId())
                    .setUserType(accessToken.getUserType())
                    // 额外的用户信息
                    .setInfo(accessToken.getUserInfo())
                    .setTenantId(accessToken.getTenantId())
                    .setScopes(accessToken.getScopes())
                    .setExpiresTime(accessToken.getExpiresTime());
        } catch (ServiceException serviceException) {
            // 校验 Token 不通过时, 考虑到一些接口是无需登录的, 所以直接返回 null 即可
            return null;
        }
    }

    /**
     * 模拟登录用户，方便日常开发调试
     *
     * <p>注意: 生产环境下, 一定要关闭该功能！</p>
     *
     * @param request  请求
     * @param token    模拟的 token, 格式为 {@link SecurityProperties#getMockSecret()} + 用户编号
     * @param userType 用户类型
     * @return 模拟的 LoginUser
     */
    private LoginUser mockLoginUser(HttpServletRequest request, String token, Integer userType) {
        if (!securityProperties.getMockEnable()) {
            return null;
        }

        // 必须以 mockSecret 开头
        if (!token.startsWith(securityProperties.getMockSecret())) {
            return null;
        }

        // 构建模拟用户
        Long userId = Long.valueOf(token.substring(securityProperties.getMockSecret().length()));
        return new LoginUser().
                setId(userId)
                .setUserType(userType)
                .setTenantId(WebFrameworkUtils.getTenantId(request));
    }

    /**
     * 从 Header 中构建 LoginUser
     *
     * @param request 请求
     * @return LoginUser
     */
    private LoginUser buildLoginUserByHeader(HttpServletRequest request) {
        String loginUserStr = request.getHeader(SecurityFrameworkUtils.LOGIN_USER_HEADER);
        if (StrUtil.isEmpty(loginUserStr)) {
            return null;
        }

        try {
            // 解码, 解决中文乱码问题
            loginUserStr = URLDecoder.decode(loginUserStr, StandardCharsets.UTF_8);
            LoginUser loginUser = JsonUtils.parseObject(loginUserStr, LoginUser.class);

            // 用户类型不匹配, 无权限
            // 注意：只有 /admin-api/* 和 /app-api/* 有 userType 才需要比对用户类型, 类似 WebSocket 的 /ws/* 连接地址, 不需要比对用户类型
            Integer userType = WebFrameworkUtils.getLoginUserType(request);
            if (userType != null && loginUser != null && ObjectUtil.notEqual(loginUser.getUserType(), userType)) {
                throw new AccessDeniedException("错误的用户类型");
            }

            return loginUser;
        } catch (Exception ex) {
            log.error("[buildLoginUserByHeader][解析 LoginUser({}) 发生异常]", loginUserStr, ex);
            ;
            throw ex;
        }
    }
}
