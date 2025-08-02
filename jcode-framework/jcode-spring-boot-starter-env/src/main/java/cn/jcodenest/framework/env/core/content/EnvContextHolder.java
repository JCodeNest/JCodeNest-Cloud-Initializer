package cn.jcodenest.framework.env.core.content;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发环境上下文
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class EnvContextHolder {

    /**
     * 标签的上下文
     * <p>
     * 使用 {@link List} 的原因是可能存在多层设置或者清理
     */
    private static final ThreadLocal<List<String>> TAG_CONTEXT = TransmittableThreadLocal.withInitial(ArrayList::new);

    /**
     * 设置标签
     *
     * @param tag 标签
     */
    public static void setTag(String tag) {
        TAG_CONTEXT.get().add(tag);
    }

    /**
     * 获取标签
     *
     * @return 标签
     */
    public static String getTag() {
        return CollUtil.getLast(TAG_CONTEXT.get());
    }

    /**
     * 清理标签
     */
    public static void removeTag() {
        List<String> tags = TAG_CONTEXT.get();
        if (CollUtil.isEmpty(tags)) {
            return;
        }

        tags.remove(tags.size() - 1);
    }
}
