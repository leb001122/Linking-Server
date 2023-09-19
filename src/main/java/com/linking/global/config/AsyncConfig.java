package com.linking.global.config;

import com.linking.global.exception.AsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    private int CORE_POOL_SIZE = 10;
    private int MAX_POOL_SIZE = 20;
    private int QUEUE_CAPACITY = 30;

    @Bean(name = "eventCallExecutor")
    public Executor eventCallExecutor() {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize( CORE_POOL_SIZE );
        taskExecutor.setMaxPoolSize( MAX_POOL_SIZE );
        taskExecutor.setQueueCapacity( QUEUE_CAPACITY );
        taskExecutor.setThreadNamePrefix( "Executor1-" );
        taskExecutor.setRejectedExecutionHandler( new ThreadPoolExecutor.CallerRunsPolicy() );
//        taskExecutor.setKeepAliveSeconds();
        taskExecutor.initialize();

        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }
}
