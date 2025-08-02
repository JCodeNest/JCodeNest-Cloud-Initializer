package cn.jcodenest.framework.dict.config;

import cn.jcodenest.framework.common.biz.system.dict.DictDataCommonApi;
import cn.jcodenest.framework.dict.core.util.DictFrameworkUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 字典自动配置
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
public class JCodeDictAutoConfiguration {

    /**
     * 初始化字典工具类 Bean
     */
    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public DictFrameworkUtils dictUtils(DictDataCommonApi dictDataApi) {
        DictFrameworkUtils.init(dictDataApi);
        return new DictFrameworkUtils();
    }
}
