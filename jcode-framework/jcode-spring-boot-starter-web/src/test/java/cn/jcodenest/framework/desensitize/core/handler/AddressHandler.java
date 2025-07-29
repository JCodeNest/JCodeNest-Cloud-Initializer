package cn.jcodenest.framework.desensitize.core.handler;

import cn.jcodenest.framework.desensitize.core.annotation.Address;
import cn.jcodenest.framework.desensitize.core.base.handler.DesensitizationHandler;

/**
 * {@link Address} 的脱敏处理器, 用于 {@link DesensitizeTest} 测试使用
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class AddressHandler implements DesensitizationHandler<Address> {

    @Override
    public String desensitize(String origin, Address annotation) {
        return origin + annotation.replacer();
    }
}
