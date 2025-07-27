package cn.jcodenest.initializer.common.util.http;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.map.TableMap;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.jcodenest.initializer.common.util.json.JsonUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Http 工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtils {

    /**
     * RestTemplate实例
     */
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    /**
     * 未知IP地址
     */
    private static final String UNKNOWN = "unknown";

    /**
     * 本地IP地址
     */
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * 发送GET请求
     *
     * @param url 请求URL
     * @return 响应字符串, 请求失败返回null
     */
    public static String get(String url) {
        return get(url, null, null);
    }

    /**
     * 发送GET请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @return 响应字符串, 请求失败返回null
     */
    public static String get(String url, Map<String, String> headers) {
        return get(url, headers, null);
    }

    /**
     * 发送GET请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @param params  请求参数
     * @return 响应字符串, 请求失败返回null
     */
    public static String get(String url, Map<String, String> headers, Map<String, Object> params) {
        try {
            HttpHeaders httpHeaders = createHttpHeaders(headers);
            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

            String requestUrl = buildUrlWithParams(url, params);
            ResponseEntity<String> response = REST_TEMPLATE.exchange(requestUrl, HttpMethod.GET, entity, String.class);

            log.debug("GET请求成功: {} -> {}", requestUrl, response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            log.error("GET请求失败: {}", url, e);
            return null;
        }
    }

    /**
     * 发送POST请求
     *
     * @param url  请求URL
     * @param body 请求体
     * @return 响应字符串, 请求失败返回null
     */
    public static String post(String url, Object body) {
        return post(url, body, null);
    }

    /**
     * 发送POST请求
     *
     * @param url     请求URL
     * @param body    请求体
     * @param headers 请求头
     * @return 响应字符串, 请求失败返回null
     */
    public static String post(String url, Object body, Map<String, String> headers) {
        try {
            HttpHeaders httpHeaders = createHttpHeaders(headers);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = body instanceof String string ? string : JsonUtils.toJsonString(body);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);

            ResponseEntity<String> response = REST_TEMPLATE.exchange(url, HttpMethod.POST, entity, String.class);

            log.debug("POST请求成功: {} -> {}", url, response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            log.error("POST请求失败: {}", url, e);
            return null;
        }
    }

    /**
     * 发送PUT请求
     *
     * @param url  请求URL
     * @param body 请求体
     * @return 响应字符串, 请求失败返回null
     */
    public static String put(String url, Object body) {
        return put(url, body, null);
    }

    /**
     * 发送PUT请求
     *
     * @param url     请求URL
     * @param body    请求体
     * @param headers 请求头
     * @return 响应字符串, 请求失败返回null
     */
    public static String put(String url, Object body, Map<String, String> headers) {
        try {
            HttpHeaders httpHeaders = createHttpHeaders(headers);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = body instanceof String string ? string : JsonUtils.toJsonString(body);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);

            ResponseEntity<String> response = REST_TEMPLATE.exchange(url, HttpMethod.PUT, entity, String.class);

            log.debug("PUT请求成功: {} -> {}", url, response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            log.error("PUT请求失败: {}", url, e);
            return null;
        }
    }

    /**
     * 发送DELETE请求
     *
     * @param url 请求URL
     * @return 响应字符串, 请求失败返回null
     */
    public static String delete(String url) {
        return delete(url, null);
    }

    /**
     * 发送DELETE请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @return 响应字符串, 请求失败返回null
     */
    public static String delete(String url, Map<String, String> headers) {
        try {
            HttpHeaders httpHeaders = createHttpHeaders(headers);
            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

            ResponseEntity<String> response = REST_TEMPLATE.exchange(url, HttpMethod.DELETE, entity, String.class);

            log.debug("DELETE请求成功: {} -> {}", url, response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            log.error("DELETE请求失败: {}", url, e);
            return null;
        }
    }

    /**
     * 获取客户端真实IP地址
     *
     * @param request HttpServletRequest
     * @return 客户端IP地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            // 多次反向代理后会有多个IP值, 第一个为真实IP
            int index = ip.indexOf(',');
            if (index != -1) {
                ip = ip.substring(0, index);
            }
            return ip.trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = request.getHeader("HTTP_CLIENT_IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = request.getRemoteAddr();
        if (LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip)) {
            // 根据网卡取本机配置的IP
            try {
                InetAddress inet = InetAddress.getLocalHost();
                ip = inet.getHostAddress();
            } catch (UnknownHostException e) {
                log.warn("获取本机IP失败", e);
            }
        }

        return ip;
    }

    /**
     * 获取用户代理字符串
     *
     * @param request HttpServletRequest
     * @return 用户代理字符串
     */
    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }

        String userAgent = request.getHeader("User-Agent");
        return StringUtils.isNotBlank(userAgent) ? userAgent : UNKNOWN;
    }

    /**
     * 判断是否为Ajax请求
     *
     * @param request HttpServletRequest
     * @return true-Ajax请求, false-非Ajax请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }

        String requestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith);
    }

    /**
     * 获取请求的完整URL
     *
     * @param request HttpServletRequest
     * @return 完整URL
     */
    public static String getFullRequestUrl(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        StringBuilder url = new StringBuilder();
        url.append(request.getScheme())
                .append("://")
                .append(request.getServerName());

        int port = request.getServerPort();
        if (port != 80 && port != 443) {
            url.append(":").append(port);
        }

        url.append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            url.append("?").append(queryString);
        }

        return url.toString();
    }

    /**
     * 创建HTTP请求头
     *
     * @param headers 请求头Map
     * @return HttpHeaders
     */
    private static HttpHeaders createHttpHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();

        if (headers != null && !headers.isEmpty()) {
            headers.forEach(httpHeaders::set);
        }

        return httpHeaders;
    }

    /**
     * 构建带参数的URL
     *
     * @param url    基础URL
     * @param params 参数Map
     * @return 带参数的URL
     */
    private static String buildUrlWithParams(String url, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder urlBuilder = new StringBuilder(url);
        boolean hasQuery = url.contains("?");

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (hasQuery) {
                urlBuilder.append("&");
            } else {
                urlBuilder.append("?");
                hasQuery = true;
            }

            urlBuilder.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
        }

        return urlBuilder.toString();
    }

    /**
     * 判断IP地址是否有效
     *
     * @param ip IP地址
     * @return true-有效, false-无效
     */
    private static boolean isValidIp(String ip) {
        return StringUtils.isNotBlank(ip) && !UNKNOWN.equalsIgnoreCase(ip);
    }

    /**
     * 编码 URL 参数
     *
     * @param value 参数
     * @return 编码后的参数
     */
    @SneakyThrows
    public static String encodeUtf8(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * 替换URL中的查询参数
     *
     * @param url   URL
     * @param key   参数名
     * @param value 参数值
     * @return 替换后的URL
     */
    @SuppressWarnings("unchecked")
    public static String replaceUrlQuery(String url, String key, String value) {
        UrlBuilder builder = UrlBuilder.of(url, Charset.defaultCharset());

        // 先移除
        TableMap<CharSequence, CharSequence> query = (TableMap<CharSequence, CharSequence>)
                ReflectUtil.getFieldValue(builder.getQuery(), "query");
        query.remove(key);

        // 后添加
        builder.addQuery(key, value);
        return builder.build();
    }

    /**
     * 拼接 URL
     *
     * @param base     基础 URL
     * @param query    查询参数
     * @param keys     query 的 key (对应的原本的 key 的映射, 例如 query 里有个 key 是 xx, 实际它的 key 是 extra_xx, 则通过 keys 里添加这个映射)
     * @param fragment URL 的 fragment, 即拼接到 # 中
     * @return 拼接后的 URL
     */
    public static String append(String base, Map<String, ?> query, Map<String, String> keys, boolean fragment) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(base);
        URI redirectUri = buildUriSafely(builder);

        UriComponentsBuilder template = UriComponentsBuilder.newInstance()
                .scheme(redirectUri.getScheme())
                .port(redirectUri.getPort())
                .host(redirectUri.getHost())
                .userInfo(redirectUri.getUserInfo())
                .path(redirectUri.getPath());

        if (fragment) {
            String fragmentValue = buildFragment(query, keys, redirectUri.getFragment());
            template.fragment(fragmentValue);
            UriComponents encoded = template.build().expand(query).encode();
            builder.fragment(encoded.getFragment());
        } else {
            addQueryParams(template, query, keys);
            template.fragment(redirectUri.getFragment());
            UriComponents encoded = template.build().expand(query).encode();
            builder.query(encoded.getQuery());
        }

        return builder.build().toUriString();
    }

    /**
     * 安全地构建 URI
     *
     * @param builder UriComponentsBuilder
     * @return URI
     */
    private static URI buildUriSafely(UriComponentsBuilder builder) {
        try {
            return builder.build(true).toUri();
        } catch (Exception e) {
            return builder.build().toUri();
        }
    }

    /**
     * 构建 URL 的 fragment
     *
     * @param query            查询参数
     * @param keys             query 的 key (对应的原本的 key 的映射, 例如 query 里有个 key 是 xx, 实际它的 key 是 extra_xx, 则通过 keys 里添加这个映射)
     * @param existingFragment 已有的 fragment
     * @return 构建后的 fragment
     */
    private static String buildFragment(Map<String, ?> query, Map<String, String> keys, String existingFragment) {
        if (query.isEmpty()) {
            return existingFragment;
        }

        StringBuilder values = new StringBuilder();
        if (existingFragment != null) {
            values.append(existingFragment);
        }

        query.forEach((key, value) -> {
            if (!values.isEmpty()) {
                values.append("&");
            }
            String name = keys != null && keys.containsKey(key) ? keys.get(key) : key;
            values.append(name).append("={").append(key).append("}");
        });

        return values.toString();
    }

    /**
     * 添加查询参数
     *
     * @param template UriComponentsBuilder
     * @param query    查询参数
     * @param keys     query 的 key (对应的原本的 key 的映射, 例如 query 里有个 key 是 xx, 实际它的 key 是 extra_xx, 则通过 keys 里添加这个映射)
     */
    private static void addQueryParams(UriComponentsBuilder template, Map<String, ?> query, Map<String, String> keys) {
        query.forEach((key, value) -> {
            String name = keys != null && keys.containsKey(key) ? keys.get(key) : key;
            template.queryParam(name, "{" + key + "}");
        });
    }

    /**
     * 从请求中获取 Basic 认证信息
     *
     * @param request 请求
     * @return Basic 认证信息
     */
    public static String[] obtainBasicAuthorization(HttpServletRequest request) {
        String clientId;
        String clientSecret;

        // 先从 Header 中获取
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        authorization = CharSequenceUtil.subAfter(authorization, "Basic ", true);

        if (org.springframework.util.StringUtils.hasText(authorization)) {
            authorization = Base64.decodeStr(authorization);
            clientId = CharSequenceUtil.subBefore(authorization, ":", false);
            clientSecret = CharSequenceUtil.subAfter(authorization, ":", false);
        } else {
            // 再从 Param 中获取
            clientId = request.getParameter("client_id");
            clientSecret = request.getParameter("client_secret");
        }

        // 如果两者非空, 则返回
        if (CharSequenceUtil.isNotEmpty(clientId) && CharSequenceUtil.isNotEmpty(clientSecret)) {
            return new String[]{clientId, clientSecret};
        }

        return new String[0];
    }
}
