package cn.jcodenest.framework.xss.core.filter;

import cn.jcodenest.framework.xss.core.clean.XssCleaner;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * XSS 请求 Wrapper
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    /**
     * XSS 清理器
     */
    private final XssCleaner xssCleaner;

    /**
     * 构造器
     *
     * @param request    请求
     * @param xssCleaner XSS 清理器
     */
    public XssRequestWrapper(HttpServletRequest request, XssCleaner xssCleaner) {
        super(request);
        this.xssCleaner = xssCleaner;
    }

    // ============================ parameter ============================

    /**
     * 获取参数
     *
     * @return 参数
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new LinkedHashMap<>();
        Map<String, String[]> parameters = super.getParameterMap();

        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            String[] values = entry.getValue();
            for (int i = 0; i < values.length; i++) {
                values[i] = xssCleaner.clean(values[i]);
            }

            map.put(entry.getKey(), values);
        }

        return map;
    }

    /**
     * 获取参数
     *
     * @return 参数
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return new String[0];
        }

        int count = values.length;
        String[] encodedValues = new String[count];

        for (int i = 0; i < count; i++) {
            encodedValues[i] = xssCleaner.clean(values[i]);
        }

        return encodedValues;
    }

    /**
     * 获取参数
     *
     * @return 参数
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }

        return xssCleaner.clean(value);
    }

    // ============================ attribute ============================

    /**
     * 获取属性
     *
     * @return 属性
     */
    @Override
    public Object getAttribute(String name) {
        Object value = super.getAttribute(name);
        if (value instanceof String valueStr) {
            return xssCleaner.clean(valueStr);
        }

        return value;
    }

    // ============================ header ============================

    /**
     * 获取头信息
     *
     * @return 头信息
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }

        return xssCleaner.clean(value);
    }

    // ============================ queryString ============================

    /**
     * 获取查询字符串
     *
     * @return 查询字符串
     */
    @Override
    public String getQueryString() {
        String value = super.getQueryString();
        if (value == null) {
            return null;
        }

        return xssCleaner.clean(value);
    }
}
