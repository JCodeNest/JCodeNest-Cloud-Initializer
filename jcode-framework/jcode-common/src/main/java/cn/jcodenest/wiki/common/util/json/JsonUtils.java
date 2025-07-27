package cn.jcodenest.wiki.common.util.json;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.PrimitiveArrayUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JSON 工具类
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
public class JsonUtils {

    /**
     * objectMapper 对象
     */
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略 null 值
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 解决 LocalDateTime 的序列化
        objectMapper.registerModules(new JavaTimeModule());
    }

    /**
     * 初始化 objectMapper 属性
     * <p>
     * 通过这种方式，使用 Spring 创建的 objectMapper Bean 来替换默认配置
     * 注意：此方法会通过反射修改 final 字段，仅在应用启动时调用
     *
     * @param mapper objectMapper 对象，不能为 null
     * @throws IllegalArgumentException 如果 mapper 参数为 null
     */
    public static void init(ObjectMapper mapper) {
        JsonUtils.objectMapper = mapper;
    }

    /**
     * 将对象转换为 JSON 字符串
     *
     * @param object 要转换的对象，可以为 null
     * @return JSON 字符串，如果对象为 null 则返回 "null"
     * @throws RuntimeException 如果序列化过程中发生错误
     */
    @SneakyThrows
    public static String toJsonString(Object object) {
        if (object == null) {
            return "null";
        }

        return objectMapper.writeValueAsString(object);
    }

    /**
     * 将对象转换为 JSON 字节数组
     *
     * @param object 要转换的对象，可以为 null
     * @return JSON 字节数组，如果对象为 null 则返回 "null" 的字节数组
     * @throws RuntimeException 如果序列化过程中发生错误
     */
    @SneakyThrows
    public static byte[] toJsonByte(Object object) {
        if (object == null) {
            return "null".getBytes();
        }

        return objectMapper.writeValueAsBytes(object);
    }

    /**
     * 将对象转换为格式化的 JSON 字符串（带缩进和换行）
     *
     * @param object 要转换的对象，可以为 null
     * @return 格式化的 JSON 字符串，如果对象为 null 则返回 "null"
     * @throws RuntimeException 如果序列化过程中发生错误
     */
    @SneakyThrows
    public static String toJsonPrettyString(Object object) {
        if (object == null) {
            return "null";
        }

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    /**
     * 将 JSON 字符串解析为指定类型的对象
     *
     * @param text  JSON 字符串，可以为空或 null
     * @param clazz 目标对象的 Class 类型，不能为 null
     * @param <T>   目标对象类型
     * @return 解析后的对象，如果 JSON 字符串为空则返回 null
     * @throws IllegalArgumentException 如果 clazz 参数为 null
     * @throws RuntimeException         如果 JSON 解析失败
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class 参数不能为 null");
        }
        if (CharSequenceUtil.isEmpty(text)) {
            return null;
        }

        try {
            return objectMapper.readValue(text, clazz);
        } catch (IOException e) {
            log.error("JSON 解析失败，JSON 内容: [{}]，目标类型: [{}]", text, clazz.getName(), e);
            throw new RuntimeException("JSON 解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从 JSON 字符串的指定路径解析为指定类型的对象
     *
     * @param text  JSON 字符串，可以为空或 null
     * @param path  JSON 路径，不能为空或 null
     * @param clazz 目标对象的 Class 类型，不能为 null
     * @param <T>   目标对象类型
     * @return 解析后的对象，如果 JSON 字符串为空则返回 null
     * @throws IllegalArgumentException 如果 path 或 clazz 参数为 null 或空
     * @throws RuntimeException         如果 JSON 解析失败
     */
    public static <T> T parseObject(String text, String path, Class<T> clazz) {
        if (CharSequenceUtil.isEmpty(path)) {
            throw new IllegalArgumentException("路径参数不能为空");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Class 参数不能为 null");
        }
        if (CharSequenceUtil.isEmpty(text)) {
            return null;
        }

        try {
            JsonNode treeNode = objectMapper.readTree(text);
            JsonNode pathNode = treeNode.path(path);
            if (pathNode.isMissingNode()) {
                log.warn("JSON 路径 [{}] 不存在，JSON 内容: [{}]", path, text);
                return null;
            }

            return objectMapper.readValue(pathNode.toString(), clazz);
        } catch (IOException e) {
            log.error("JSON 路径解析失败，JSON 内容: [{}]，路径: [{}]，目标类型: [{}]", text, path, clazz.getName(), e);
            throw new RuntimeException("JSON 路径解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定 Type 类型的对象
     *
     * @param text JSON 字符串，可以为空或 null
     * @param type 目标对象的 Type 类型，不能为 null
     * @param <T>  目标对象类型
     * @return 解析后的对象，如果 JSON 字符串为空则返回 null
     * @throws IllegalArgumentException 如果 type 参数为 null
     * @throws RuntimeException         如果 JSON 解析失败
     */
    public static <T> T parseObject(String text, Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Type 参数不能为 null");
        }
        if (CharSequenceUtil.isEmpty(text)) {
            return null;
        }

        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructType(type));
        } catch (IOException e) {
            log.error("JSON 解析失败，JSON 内容: [{}]，目标类型: [{}]", text, type.getTypeName(), e);
            throw new RuntimeException("JSON 解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定类型的对象（兼容模式）
     * <p>
     * 使用 {@link #parseObject(String, Class)} 时，在 @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) 的场景下，
     * 如果 JSON 字符串没有 class 属性，则会报错。此时使用这个方法可以解决该问题。
     * 该方法使用 Hutool 的 JSONUtil 进行解析，兼容性更好。
     *
     * @param text  JSON 字符串，可以为空或 null
     * @param clazz 目标对象的 Class 类型，不能为 null
     * @param <T>   目标对象类型
     * @return 解析后的对象，如果 JSON 字符串为空则返回 null
     * @throws IllegalArgumentException 如果 clazz 参数为 null
     * @throws RuntimeException         如果 JSON 解析失败
     */
    public static <T> T parseObject2(String text, Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class 参数不能为 null");
        }
        if (CharSequenceUtil.isEmpty(text)) {
            return null;
        }

        try {
            return JSONUtil.toBean(text, clazz);
        } catch (Exception e) {
            log.error("JSON 兼容解析失败，JSON 内容: [{}]，目标类型: [{}]", text, clazz.getName(), e);
            throw new RuntimeException("JSON 兼容解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON 字节数组解析为指定类型的对象
     *
     * @param bytes JSON 字节数组，可以为空或 null
     * @param clazz 目标对象的 Class 类型，不能为 null
     * @param <T>   目标对象类型
     * @return 解析后的对象，如果字节数组为空则返回 null
     * @throws IllegalArgumentException 如果 clazz 参数为 null
     * @throws RuntimeException         如果 JSON 解析失败
     */
    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class 参数不能为 null");
        }
        if (PrimitiveArrayUtil.isEmpty(bytes)) {
            return null;
        }

        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            log.error("JSON 字节数组解析失败，字节数组长度: [{}]，目标类型: [{}]", bytes.length, clazz.getName(), e);
            throw new RuntimeException("JSON 字节数组解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定 TypeReference 类型的对象
     * <p>
     * 适用于泛型类型的解析，如 List&lt;User&gt;、Map&lt;String, Object&gt; 等
     *
     * @param text          JSON 字符串，不能为空或 null
     * @param typeReference 类型引用，不能为 null
     * @param <T>           目标对象类型
     * @return 解析后的对象
     * @throws IllegalArgumentException 如果参数为 null 或空
     * @throws RuntimeException         如果 JSON 解析失败
     */
    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        if (CharSequenceUtil.isEmpty(text)) {
            throw new IllegalArgumentException("JSON 字符串不能为空");
        }
        if (typeReference == null) {
            throw new IllegalArgumentException("TypeReference 参数不能为 null");
        }

        try {
            return objectMapper.readValue(text, typeReference);
        } catch (IOException e) {
            log.error("JSON TypeReference 解析失败，JSON 内容: [{}]，目标类型: [{}]", text, typeReference.getType().getTypeName(), e);
            throw new RuntimeException("JSON TypeReference 解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 静默解析 JSON 字符串为指定 TypeReference 类型的对象
     * <p>
     * 如果解析失败，不会抛出异常，而是返回 null。适用于不确定 JSON 格式是否正确的场景。
     *
     * @param text          JSON 字符串，可以为空或 null
     * @param typeReference 类型引用，不能为 null
     * @param <T>           目标对象类型
     * @return 解析后的对象，如果解析失败或字符串为空则返回 null
     * @throws IllegalArgumentException 如果 typeReference 参数为 null
     */
    public static <T> T parseObjectQuietly(String text, TypeReference<T> typeReference) {
        if (typeReference == null) {
            throw new IllegalArgumentException("TypeReference 参数不能为 null");
        }
        if (CharSequenceUtil.isEmpty(text)) {
            return null;
        }

        try {
            return objectMapper.readValue(text, typeReference);
        } catch (IOException e) {
            log.debug("JSON 静默解析失败，JSON 内容: [{}]，目标类型: [{}]", text, typeReference.getType().getTypeName(), e);
            return null;
        }
    }

    /**
     * 将 JSON 字符串解析为指定类型的对象列表
     *
     * @param text  JSON 数组字符串，可以为空或 null
     * @param clazz 列表元素的 Class 类型，不能为 null
     * @param <T>   列表元素类型
     * @return 解析后的对象列表，如果 JSON 字符串为空则返回空列表
     * @throws IllegalArgumentException 如果 clazz 参数为 null
     * @throws RuntimeException         如果 JSON 解析失败
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class 参数不能为 null");
        }
        if (CharSequenceUtil.isEmpty(text)) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.error("JSON 数组解析失败，JSON 内容: [{}]，元素类型: [{}]", text, clazz.getName(), e);
            throw new RuntimeException("JSON 数组解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从 JSON 字符串的指定路径解析为指定类型的对象列表
     *
     * @param text  JSON 字符串，可以为空或 null
     * @param path  JSON 路径，不能为空或 null
     * @param clazz 列表元素的 Class 类型，不能为 null
     * @param <T>   列表元素类型
     * @return 解析后的对象列表，如果 JSON 字符串为空或路径不存在则返回 null
     * @throws IllegalArgumentException 如果 path 或 clazz 参数为 null 或空
     * @throws RuntimeException         如果 JSON 解析失败
     */
    public static <T> List<T> parseArray(String text, String path, Class<T> clazz) {
        if (CharSequenceUtil.isEmpty(path)) {
            throw new IllegalArgumentException("路径参数不能为空");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Class 参数不能为 null");
        }
        if (CharSequenceUtil.isEmpty(text)) {
            return Collections.emptyList();
        }

        try {
            JsonNode treeNode = objectMapper.readTree(text);
            JsonNode pathNode = treeNode.path(path);
            if (pathNode.isMissingNode()) {
                log.warn("JSON 路径 [{}] 不存在，JSON 内容: [{}]", path, text);
                return Collections.emptyList();
            }

            return objectMapper.readValue(pathNode.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.error("JSON 数组路径解析失败，JSON 内容: [{}]，路径: [{}]，元素类型: [{}]", text, path, clazz.getName(), e);
            throw new RuntimeException("JSON 数组路径解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON 字符串解析为 JsonNode 树结构
     *
     * @param text JSON 字符串，不能为空或 null
     * @return JsonNode 对象，可以用于遍历和操作 JSON 结构
     * @throws IllegalArgumentException 如果 text 参数为空或 null
     * @throws RuntimeException         如果 JSON 解析失败
     */
    public static JsonNode parseTree(String text) {
        if (CharSequenceUtil.isEmpty(text)) {
            throw new IllegalArgumentException("JSON 字符串不能为空");
        }

        try {
            return objectMapper.readTree(text);
        } catch (IOException e) {
            log.error("JSON 树解析失败，JSON 内容: [{}]", text, e);
            throw new RuntimeException("JSON 树解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON 字节数组解析为 JsonNode 树结构
     *
     * @param bytes JSON 字节数组，不能为空或 null
     * @return JsonNode 对象，可以用于遍历和操作 JSON 结构
     * @throws IllegalArgumentException 如果 bytes 参数为空或 null
     * @throws RuntimeException         如果 JSON 解析失败
     */
    public static JsonNode parseTree(byte[] bytes) {
        if (PrimitiveArrayUtil.isEmpty(bytes)) {
            throw new IllegalArgumentException("JSON 字节数组不能为空");
        }

        try {
            return objectMapper.readTree(bytes);
        } catch (IOException e) {
            log.error("JSON 字节数组树解析失败，字节数组长度: [{}]", bytes.length, e);
            throw new RuntimeException("JSON 字节数组树解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 判断字符串是否为有效的 JSON 格式
     * <p>
     * 支持 JSON 对象、JSON 数组、JSON 基本类型等所有 JSON 格式
     *
     * @param text 要检查的字符串，可以为空或 null
     * @return true 如果是有效的 JSON 格式，false 否则
     */
    public static boolean isJson(String text) {
        if (CharSequenceUtil.isEmpty(text)) {
            return false;
        }

        return JSONUtil.isTypeJSON(text);
    }

    /**
     * 判断字符串是否为 JSON 对象格式
     * <p>
     * 只检查是否为 JSON 对象（以 { 开头，以 } 结尾），不包括 JSON 数组或基本类型
     *
     * @param str 要检查的字符串，可以为空或 null
     * @return true 如果是 JSON 对象格式，false 否则
     */
    public static boolean isJsonObject(String str) {
        if (CharSequenceUtil.isEmpty(str)) {
            return false;
        }

        return JSONUtil.isTypeJSONObject(str);
    }

    // ========== 新增实用方法 ==========

    /**
     * 静默将对象转换为 JSON 字符串
     * <p>
     * 如果转换失败，不会抛出异常，而是返回 null。适用于不确定对象是否可序列化的场景。
     *
     * @param object 要转换的对象，可以为 null
     * @return JSON 字符串，如果转换失败则返回 null
     */
    public static String toJsonStringQuietly(Object object) {
        if (object == null) {
            return "null";
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.debug("对象静默序列化失败，对象类型: [{}]", object.getClass().getName(), e);
            return null;
        }
    }

    /**
     * 静默解析 JSON 字符串为指定类型的对象
     * <p>
     * 如果解析失败，不会抛出异常，而是返回 null。适用于不确定 JSON 格式是否正确的场景。
     *
     * @param text  JSON 字符串，可以为空或 null
     * @param clazz 目标对象的 Class 类型，不能为 null
     * @param <T>   目标对象类型
     * @return 解析后的对象，如果解析失败或字符串为空则返回 null
     * @throws IllegalArgumentException 如果 clazz 参数为 null
     */
    public static <T> T parseObjectQuietly(String text, Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class 参数不能为 null");
        }
        if (CharSequenceUtil.isEmpty(text)) {
            return null;
        }

        try {
            return objectMapper.readValue(text, clazz);
        } catch (IOException e) {
            log.debug("JSON 静默解析失败，JSON 内容: [{}]，目标类型: [{}]", text, clazz.getName(), e);
            return null;
        }
    }

    /**
     * 验证 JSON 字符串格式的有效性
     * <p>
     * 通过实际解析来验证 JSON 格式，比 {@link #isJson(String)} 更严格
     *
     * @param text 要验证的 JSON 字符串，可以为空或 null
     * @return true 如果 JSON 格式有效，false 否则
     */
    public static boolean isValidJson(String text) {
        if (CharSequenceUtil.isEmpty(text)) {
            return false;
        }

        try {
            objectMapper.readTree(text);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 通过 JSON 序列化和反序列化实现对象深拷贝
     * <p>
     * 注意：被拷贝的对象必须支持 JSON 序列化和反序列化
     *
     * @param source 源对象，不能为 null
     * @param clazz  目标对象的 Class 类型，不能为 null
     * @param <T>    对象类型
     * @return 深拷贝后的对象
     * @throws IllegalArgumentException 如果参数为 null
     * @throws RuntimeException         如果深拷贝失败
     */
    public static <T> T deepCopy(T source, Class<T> clazz) {
        if (source == null) {
            throw new IllegalArgumentException("源对象不能为 null");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Class 参数不能为 null");
        }

        try {
            String json = objectMapper.writeValueAsString(source);
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error("对象深拷贝失败，对象类型: [{}]", clazz.getName(), e);
            throw new RuntimeException("对象深拷贝失败: " + e.getMessage(), e);
        }
    }
}
