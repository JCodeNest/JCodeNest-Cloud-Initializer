package cn.jcodenest.initializer.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文档地址枚举
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Getter
@AllArgsConstructor
public enum DocumentEnum {

    REDIS_INSTALL("https://gitee.com/zhijiantianya/ruoyi-vue-pro/issues/I4VCSJ", "Redis 安装文档"),
    TENANT("https://doc.iocoder.cn", "SaaS 多租户文档");

    /**
     * 文档 URL
     */
    private final String url;

    /**
     * 备注
     */
    private final String memo;
}
