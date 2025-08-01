package cn.jcodenest.framework.operatelog.config;

import cn.jcodenest.framework.operatelog.core.service.LogRecordServiceImpl;
import com.mzt.logapi.service.ILogRecordService;
import com.mzt.logapi.starter.annotation.EnableLogRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 操作日志配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@AutoConfiguration
@EnableLogRecord(tenant = "") // tenant 给空即可, 这里没有什么实际应用场景
public class JCodeOperateLogConfiguration {

    @Bean
    @Primary
    public ILogRecordService iLogRecordServiceImpl() {
        return new LogRecordServiceImpl();
    }
}
