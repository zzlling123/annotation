package com.xinkao.erp.core.config;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.xinkao.erp.common.config.properties.XinKaoProperties;
import com.xinkao.erp.common.util.ThreadUtil;

@EnableAsync
@EnableScheduling
@Configuration
public class AsynTaskConfig implements AsyncConfigurer{
	
	@Resource
    private XinKaoProperties xinKaoProperties;
    @Bean(name = "scheduledExecutorService")
    public ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(xinKaoProperties.getThreadPool().getCorePoolSize(),
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                ThreadUtil.printException(r, t);
            }
        };
    }
    
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	  	executor.setDaemon(true);
	  	executor.setThreadNamePrefix("asyn-pool-");
        executor.setMaxPoolSize(16);
        executor.setCorePoolSize(4);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(300);
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        executor.setRejectedExecutionHandler(handler);
        return executor;
    }
    
	@Override
	public Executor getAsyncExecutor() {
        return threadPoolTaskExecutor();
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return AsyncConfigurer.super.getAsyncUncaughtExceptionHandler();
	}
}
