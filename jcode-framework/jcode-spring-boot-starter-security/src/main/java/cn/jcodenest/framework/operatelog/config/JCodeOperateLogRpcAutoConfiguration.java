package cn.jcodenest.framework.operatelog.config;

import cn.jcodenest.framework.common.biz.system.logger.OperateLogCommonApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * OperateLog 使用到 Feign 的配置项
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
@EnableFeignClients(clients = {OperateLogCommonApi.class}) // 主要是引入相关的 API 服务
public class JCodeOperateLogRpcAutoConfiguration {
}
