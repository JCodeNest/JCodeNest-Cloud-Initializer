package cn.jcodenest.framework.security.core.handler;

import cn.jcodenest.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.jcodenest.framework.common.pojo.CommonResult;
import cn.jcodenest.framework.common.util.servlet.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import java.io.IOException;

/**
 * 访问一个需要认证的 URL 资源, 但是此时自己尚未认证（登录）的情况下返回 {@link GlobalErrorCodeConstants#UNAUTHORIZED} 错误码, 从而使前端重定向到登录页
 *
 * <p>补充：Spring Security 通过 {@link ExceptionTranslationFilter} 的 sendStartAuthentication 方法调用当前类</p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@SuppressWarnings("JavadocReference") // 忽略文档引用报错
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    /**
     * 认证失败处理
     *
     * @param request       请求
     * @param response      响应
     * @param authException 异常
     * @throws IOException      IO 异常
     * @throws ServletException 服务端异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.debug("[commence][访问 URL({}) 时，没有登录]", request.getRequestURI(), authException);
        // 返回 401
        ServletUtils.writeJSON(response, CommonResult.error(GlobalErrorCodeConstants.UNAUTHORIZED));
    }
}
