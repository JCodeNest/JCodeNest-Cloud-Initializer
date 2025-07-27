package cn.jcodenest.initializer.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RpcConstants {

    /**
     * RPC API 前缀
     */
    public static final String RPC_API_PREFIX = "/rpc-api";

    /**
     * system 服务名
     * 注意: 需要保证和 spring.application.name 保持一致
     */
    public static final String SYSTEM_NAME = "system-server";

    /**
     * system 服务的前缀
     */
    public static final String SYSTEM_PREFIX = RPC_API_PREFIX + "/system";

    /**
     * infra 服务名
     * 注意: 需要保证和 spring.application.name 保持一致
     */
    public static final String INFRA_NAME = "infra-server";

    /**
     * infra 服务的前缀
     */
    public static final String INFRA_PREFIX = RPC_API_PREFIX + "/infra";
}
