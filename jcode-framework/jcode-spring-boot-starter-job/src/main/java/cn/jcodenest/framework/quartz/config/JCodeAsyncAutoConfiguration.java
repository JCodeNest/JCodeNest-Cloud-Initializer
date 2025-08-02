package cn.jcodenest.framework.quartz.config;

import com.alibaba.ttl.TtlRunnable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步任务自动配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@EnableAsync
@AutoConfiguration
public class JCodeAsyncAutoConfiguration {

    /**
     * 处理 ThreadPoolTaskExecutor 和 SimpleAsyncTaskExecutor
     */
    @Bean
    public BeanPostProcessor threadPoolTaskExecutorBeanPostProcessor() {
        return new BeanPostProcessor() {

            /**
             * 在 Bean 初始化之前，对 ThreadPoolTaskExecutor 和 SimpleAsyncTaskExecutor 进行处理
             *
             * @param bean     要初始化的 Bean
             * @param beanName Bean 的名称
             * @return 处理后的 Bean
             * @throws BeansException 如果在处理过程中发生错误
             */
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                // 处理 ThreadPoolTaskExecutor
                if (bean instanceof ThreadPoolTaskExecutor executor) {
                    executor.setTaskDecorator(TtlRunnable::get);
                    return executor;
                }

                // 处理 SimpleAsyncTaskExecutor
                // 参考 https://t.zsxq.com/CBoks 增加
                if (bean instanceof SimpleAsyncTaskExecutor executor) {
                    executor.setTaskDecorator(TtlRunnable::get);
                    return executor;
                }

                return bean;
            }
        };
    }
}
