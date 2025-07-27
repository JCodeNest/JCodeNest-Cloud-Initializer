package cn.jcodenest.initializer.common.util.monitor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

/**
 * 链路追踪工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TracerUtils {

    /**
     * 获取链路追踪编号, 直接返回 SkyWalking 的 TraceId, 如果不存在则为空字符串
     *
     * @return 链路追踪编号
     */
    public static String getTraceId() {
        return TraceContext.traceId();
    }
}
