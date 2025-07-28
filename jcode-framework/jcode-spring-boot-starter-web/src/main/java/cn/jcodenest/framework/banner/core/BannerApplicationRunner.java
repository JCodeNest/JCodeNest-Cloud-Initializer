package cn.jcodenest.framework.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.concurrent.TimeUnit;

/**
 * 应用启动成功输出文档地址
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/28
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    private static final String BANNER_MESSAGE = """
            ----------------------------------------------------------
            项目启动成功！
            接口文档: \t{}
            开发文档: \t{}
            视频教程: \t{}
            ----------------------------------------------------------
            """;

    private static final String[] MODULE_MESSAGES = {
            "[报表模块 jcode-module-report 教程][参考 {} 开启]",
            "[工作流模块 jcode-module-bpm 教程][参考 {} 开启]",
            "[商城系统 jcode-module-mall 教程][参考 {} 开启]",
            "[ERP 系统 jcode-module-erp - 教程][参考 {} 开启]",
            "[CRM 系统 jcode-module-crm - 教程][参考 {} 开启]",
            "[微信公众号 jcode-module-mp 教程][参考 {} 开启]",
            "[支付系统 jcode-module-pay - 教程][参考 {} 开启]",
            "[AI 大模型 jcode-module-ai - 教程][参考 {} 开启]"
    };

    private static final String[] MODULE_URLS = {
            "https://www.jcodenest.cn/report/",
            "https://www.jcodenest.cn/bpm/",
            "https://www.jcodenest.cn/mall/build/",
            "https://www.jcodenest.cn/erp/build/",
            "https://www.jcodenest.cn/crm/build/",
            "https://www.jcodenest.cn/mp/build/",
            "https://www.jcodenest.cn/pay/build/",
            "https://www.jcodenest.cn/ai/build/"
    };

    /**
     * 应用启动成功输出文档地址
     *
     * @param args 应用启动参数
     * @throws Exception 异常
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        ThreadUtil.execute(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS);

            log.info(BANNER_MESSAGE,
                    "https://www.jcodenest.cn/api-doc/",
                    "https://www.jcodenest.cn",
                    "https://t.zsxq.com/02Yf6M7Qn");

            for (int i = 0; i < MODULE_MESSAGES.length; i++) {
                log.info(MODULE_MESSAGES[i], MODULE_URLS[i]);
            }
        });
    }
}
