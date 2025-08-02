package cn.jcodenest.framework.dict.config;

import cn.jcodenest.framework.common.biz.system.dict.DictDataCommonApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 字典用到 Feign 的配置项
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
@EnableFeignClients(clients = DictDataCommonApi.class) // 主要是引入相关的 API 服务
public class JCodeDictRpcAutoConfiguration {
}
