package com.xinkao.erp.common.config.properties;

import lombok.Data;

@Data
public class ThreadPoolProperties {

    private boolean enabled;

    private int corePoolSize;

    private int maxPoolSize;
    private int queueCapacity;

    private int keepAliveSeconds;

    private String rejectedExecutionHandler;

}
