package cn.jcodenest.framework.tracer.core.annotation;

import java.lang.annotation.*;

/**
 * 打印业务编号 / 业务类型注解
 *
 * <p>
 *     使用时，需要设置 SkyWalking OAP Server 的 application.yaml 配置文件，
 *     修改 SW_SEARCHABLE_TAG_KEYS 配置项，增加 biz.type 和 biz.id 两值，
 *     然后重启 SkyWalking OAP Server 服务器。
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface BizTrace {

    /**
     * 业务编号 tag 名
     */
    String ID_TAG = "biz.id";

    /**
     * 业务类型 tag 名
     */
    String TYPE_TAG = "biz.type";

    /**
     * @return 操作名
     */
    String operationName() default "";

    /**
     * @return 业务编号
     */
    String id();

    /**
     * @return 业务类型
     */
    String type();
}
