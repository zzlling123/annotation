package com.xinkao.erp.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.xinkao.erp.common.config.properties.XinKaoProperties;

/**
 * 项目相关配置
 **/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(XinKaoProperties.class)
public class XinKaoConfig {

    private final XinKaoProperties xinKaoProperties;

    public XinKaoConfig(XinKaoProperties xinKaoProperties) {
        this.xinKaoProperties = xinKaoProperties;
    }
}
