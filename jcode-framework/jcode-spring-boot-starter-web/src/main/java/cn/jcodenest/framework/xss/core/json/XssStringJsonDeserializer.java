package cn.jcodenest.framework.xss.core.json;

import cn.jcodenest.framework.common.util.servlet.ServletUtils;
import cn.jcodenest.framework.xss.config.XssProperties;
import cn.jcodenest.framework.xss.core.clean.XssCleaner;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PathMatcher;

import java.io.IOException;

/**
 * XSS 过滤 Jackson 反序列化器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@AllArgsConstructor
public class XssStringJsonDeserializer extends StringDeserializer {

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
     * 反序列化
     *
     * @param p    JSON 解析器
     * @param ctxt 反序列化上下文
     * @return 反序列化后的字符串
     * @throws IOException 如果反序列化过程中发生 I/O 错误
     */
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // 1. 白名单 URL 的处理
        HttpServletRequest request = ServletUtils.getRequest();
        if (request != null) {
            String uri = ServletUtils.getRequest().getRequestURI();
            if (properties.getExcludeUrls().stream().anyMatch(excludeUrl -> pathMatcher.match(excludeUrl, uri))) {
                return p.getText();
            }
        }

        // 2. 真正使用 xssCleaner 进行过滤
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return xssCleaner.clean(p.getText());
        }

        JsonToken t = p.currentToken();

        // [databind#381]
        if (t == JsonToken.START_ARRAY) {
            return _deserializeFromArray(p, ctxt);
        }

        // need to gracefully handle byte[] data, as base64
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object ob = p.getEmbeddedObject();
            if (ob == null) {
                return null;
            }

            if (ob instanceof byte[] bytes) {
                return ctxt.getBase64Variant().encode(bytes, false);
            }

            // otherwise, try conversion using toString()...
            return ob.toString();
        }

        // 29-Jun-2020, tatu: New! "Scalar from Object" (mostly for XML)
        if (t == JsonToken.START_OBJECT) {
            return ctxt.extractScalarFromObject(p, this, _valueClass);
        }

        if (t.isScalarValue()) {
            String text = p.getValueAsString();
            return xssCleaner.clean(text);
        }

        return (String) ctxt.handleUnexpectedToken(_valueClass, p);
    }
}
