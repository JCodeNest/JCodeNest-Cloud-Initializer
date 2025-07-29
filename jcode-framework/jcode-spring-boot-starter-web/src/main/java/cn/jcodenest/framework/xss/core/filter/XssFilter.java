package cn.jcodenest.framework.xss.core.filter;

import cn.jcodenest.framework.xss.config.XssProperties;
import cn.jcodenest.framework.xss.core.clean.XssCleaner;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * XSS 过滤器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AllArgsConstructor
public class XssFilter extends OncePerRequestFilter {

    /**
     * XSS 属性配置
     */
    private final XssProperties properties;

    /**
     * 路径匹配器
     */
    private final PathMatcher pathMatcher;

    /**
     * XSS 清理器
     */
    private final XssCleaner xssCleaner;

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
        filterChain.doFilter(new XssRequestWrapper(request, xssCleaner), response);
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
        // 如果关闭, 则不过滤
        if (!properties.isEnable()) {
            return true;
        }

        // 如果匹配到无需过滤, 则不过滤
        String uri = request.getRequestURI();
        return properties.getExcludeUrls().stream()
                .anyMatch(excludeUrl -> pathMatcher.match(excludeUrl, uri));
    }
}
