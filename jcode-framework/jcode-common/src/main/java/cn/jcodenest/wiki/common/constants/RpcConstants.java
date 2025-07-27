package cn.jcodenest.wiki.common.constants;

/**
 * RPC 常量
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public interface RpcConstants {

    /**
     * RPC API 前缀
     */
    String RPC_API_PREFIX = "/rpc-api";

    /**
     * system 服务名
     * 注意: 需要保证和 spring.application.name 保持一致
     */
    String SYSTEM_NAME = "system-server";

    /**
     * system 服务的前缀
     */
    String SYSTEM_PREFIX = RPC_API_PREFIX + "/system";

    /**
     * infra 服务名
     * 注意: 需要保证和 spring.application.name 保持一致
     */
    String INFRA_NAME = "infra-server";

    /**
     * infra 服务的前缀
     */
    String INFRA_PREFIX = RPC_API_PREFIX + "/infra";
}
