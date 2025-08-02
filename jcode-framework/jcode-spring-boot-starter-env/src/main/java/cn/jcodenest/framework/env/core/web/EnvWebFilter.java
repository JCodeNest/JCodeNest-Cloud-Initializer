package cn.jcodenest.framework.env.core.web;

import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.env.core.content.EnvContextHolder;
import cn.jcodenest.framework.env.core.util.EnvUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 环境的 {@link jakarta.servlet.Filter} 实现类
 * 当有 tag 请求头时，设置到 {@link EnvContextHolder} 的标签上下文
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class EnvWebFilter extends OncePerRequestFilter {

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
        // 如果没有 tag，则走默认的流程
        String tag = EnvUtils.getTag(request);
        if (StrUtil.isEmpty(tag)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 如果有 tag，则设置到上下文
        EnvContextHolder.setTag(tag);
        try {
            filterChain.doFilter(request, response);
        } finally {
            EnvContextHolder.removeTag();
        }
    }
}
