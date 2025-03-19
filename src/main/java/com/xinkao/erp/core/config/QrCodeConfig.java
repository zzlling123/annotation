package com.xinkao.erp.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.hutool.core.img.ColorUtil;
import cn.hutool.extra.qrcode.QrConfig;
/**
 * 二维码配置
 * @author hys_thanks
 *
 */
@Configuration
public class QrCodeConfig {
   
    @Bean
    public QrConfig qrConfig(){
        QrConfig qrConfig=new QrConfig();
        qrConfig.setBackColor(ColorUtil.getColor("WHITE"));
        qrConfig.setForeColor(ColorUtil.getColor("BLACK"));
        return qrConfig;
    }
}
