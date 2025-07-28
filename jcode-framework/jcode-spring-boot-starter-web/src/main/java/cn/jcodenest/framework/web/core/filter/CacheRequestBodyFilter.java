package cn.jcodenest.framework.web.core.filter;

import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.common.util.servlet.ServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Request Body 缓存过滤器, 实现请求体可重复读取
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/28
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class CacheRequestBodyFilter extends OncePerRequestFilter {

    /**
     * 忽略的 URI, 排除 Spring Boot Admin 相关请求, 避免客户端连接中断导致的异常.
     */
    private static final String[] IGNORE_URIS = {"/admin/", "/actuator/"};

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
        // 缓存 Request Body 后继续处理过滤链
        filterChain.doFilter(new CacheRequestBodyWrapper(request), response);
    }

    /**
     * 是否过滤
     *
     * @param request 请求
     * @return true-过滤, false-不过滤
     * @throws ServletException 服务端异常
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 如果是忽略的 URI, 则不进行过滤
        String requestUri = request.getRequestURI();
        if (StrUtil.startWithAny(requestUri, IGNORE_URIS)) {
            return true;
        }

        // 只处理 JSON 请求
        return !ServletUtils.isJsonRequest(request);
    }
}
