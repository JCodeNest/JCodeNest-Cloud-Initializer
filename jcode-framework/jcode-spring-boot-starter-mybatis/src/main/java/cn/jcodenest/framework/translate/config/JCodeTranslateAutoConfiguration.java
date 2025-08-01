package cn.jcodenest.framework.translate.config;

import cn.jcodenest.framework.translate.core.TranslateUtils;
import com.fhs.trans.service.impl.TransService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * VO 翻译配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
public class JCodeTranslateAutoConfiguration {

    /**
     * 注册 TranslateUtils Bean, 用于方便的进行 VO 数据翻译
     */
    @Bean
    @SuppressWarnings({"InstantiationOfUtilityClass", "SpringJavaInjectionPointsAutowiringInspection"})
    public TranslateUtils translateUtils(TransService transService) {
        TranslateUtils.init(transService);
        return new TranslateUtils();
    }
}
