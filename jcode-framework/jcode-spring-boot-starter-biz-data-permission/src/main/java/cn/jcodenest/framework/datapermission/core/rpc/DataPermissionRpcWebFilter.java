package cn.jcodenest.framework.datapermission.core.rpc;

import cn.jcodenest.framework.datapermission.core.aop.DataPermissionContextHolder;
import cn.jcodenest.framework.datapermission.core.util.DataPermissionUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * 针对 {@link DataPermissionRequestInterceptor} 的 RPC 调用
 * 设置 {@link DataPermissionContextHolder} 的上下文
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class DataPermissionRpcWebFilter extends OncePerRequestFilter {

    /**
     * 拦截器
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String enable = request.getHeader(DataPermissionRequestInterceptor.ENABLE_HEADER_NAME);
        if (Objects.equals(enable, Boolean.FALSE.toString())) {
            DataPermissionUtils.executeIgnore(() -> {
                try {
                    filterChain.doFilter(request, response);
                } catch (IOException | ServletException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
