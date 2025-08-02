package cn.jcodenest.framework.env.core.feign;

import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.env.core.content.EnvContextHolder;
import cn.jcodenest.framework.env.core.util.EnvUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 多环境的 {@link RequestInterceptor} 实现类
 * Feign 请求时，将 tag 设置到 header 中，继续透传给被调用的服务
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class EnvRequestInterceptor implements RequestInterceptor {

    /**
     * 重写 apply 方法
     *
     * @param requestTemplate 请求模板
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        String tag = EnvContextHolder.getTag();
        if (StrUtil.isNotEmpty(tag)) {
            EnvUtils.setTag(requestTemplate, tag);
        }
    }
}
