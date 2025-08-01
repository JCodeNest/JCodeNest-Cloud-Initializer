package cn.jcodenest.framework.security.core.handler;

import cn.jcodenest.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.jcodenest.framework.common.pojo.CommonResult;
import cn.jcodenest.framework.common.util.servlet.ServletUtils;
import cn.jcodenest.framework.security.core.util.SecurityFrameworkUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import java.io.IOException;

/**
 * 访问一个需要认证的 URL 资源, 已经认证（登录）但是没有权限的情况下 返回 {@link GlobalErrorCodeConstants#FORBIDDEN} 错误码
 *
 * <p>补充：Spring Security 通过 {@link ExceptionTranslationFilter} 的 handleAccessDeniedException 方法调用当前类</p>
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
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    /**
     * 处理权限不足异常
     *
     * @param request               请求
     * @param response              响应
     * @param accessDeniedException 权限不足异常
     * @throws IOException      IO 异常
     * @throws ServletException 服务端异常
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 打印 warn 的原因是不定期合并 warn, 看看有没恶意破坏
        log.warn("[commence][访问 URL({}) 时，用户({}) 权限不够]", request.getRequestURI(), SecurityFrameworkUtils.getLoginUserId(), accessDeniedException);
        // 返回 403
        ServletUtils.writeJSON(response, CommonResult.error(GlobalErrorCodeConstants.FORBIDDEN));
    }
}
