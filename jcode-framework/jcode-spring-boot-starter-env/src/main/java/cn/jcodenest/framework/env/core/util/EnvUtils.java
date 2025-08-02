package cn.jcodenest.framework.env.core.util;

import cn.jcodenest.framework.env.config.properties.EnvProperties;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.Objects;

/**
 * 环境工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class EnvUtils {

    /**
     * 环境标签的在 HEADER 的键
     */
    private static final String HEADER_TAG = "tag";

    /**
     * 环境标签的特殊值，表示使用本地的主机名作为标签
     */
    public static final String HOST_NAME_VALUE = "${HOSTNAME}";

    /**
     * 从请求中获取环境标签
     *
     * @param request 请求
     * @return 环境标签
     */
    public static String getTag(HttpServletRequest request) {
        String tag = request.getHeader(HEADER_TAG);
        // 如果请求的是 "${HOSTNAME}"，则解析成对应的本地主机名
        // 目的：特殊逻辑，解决 IDEA Rest Client 不支持环境变量的读取，所以就服务器来做
        return Objects.equals(tag, HOST_NAME_VALUE) ? getHostName() : tag;
    }

    /**
     * 从服务实例中获取环境标签
     *
     * @param instance 服务实例
     * @return 环境标签
     */
    public static String getTag(ServiceInstance instance) {
        return instance.getMetadata().get(HEADER_TAG);
    }

    /**
     * 从环境变量中获取环境标签
     *
     * @param environment 环境变量
     * @return 环境标签
     */
    public static String getTag(Environment environment) {
        String tag = environment.getProperty(EnvProperties.TAG_KEY);
        // 如果请求的是 "${HOSTNAME}"，则解析成对应的本地主机名
        // 目的：特殊逻辑，解决 IDEA Rest Client 不支持环境变量的读取，所以就服务器来做
        return Objects.equals(tag, HOST_NAME_VALUE) ? getHostName() : tag;
    }

    /**
     * 设置环境标签
     *
     * @param requestTemplate 请求模板
     * @param tag             环境标签
     */
    public static void setTag(RequestTemplate requestTemplate, String tag) {
        requestTemplate.header(HEADER_TAG, tag);
    }

    /**
     * 获取 hostname 主机名
     *
     * @return 主机名
     */
    @SneakyThrows
    public static String getHostName() {
        return InetAddress.getLocalHost().getHostName();
    }
}
