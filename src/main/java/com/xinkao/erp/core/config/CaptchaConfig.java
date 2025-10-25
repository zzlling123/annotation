package com.xinkao.erp.core.config;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import java.awt.Color;
import java.awt.Font;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaptchaConfig {

    private final int width = 160;
    private final int height = 60;
    private final Color background = Color.PINK;
    private final Font font = new Font("Arial", Font.PLAIN, 48);

    @Bean(name = "CircleCaptcha")
    public CircleCaptcha getCircleCaptcha() {
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(width, height);
        captcha.setBackground(background);
        captcha.setFont(font);
        return captcha;
    }

    @Bean(name = "LineCaptcha")
    public LineCaptcha getLineCaptcha() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(width, height);
        captcha.setBackground(background);
        captcha.setFont(font);
        return captcha;
    }
    @Bean(name = "ShearCaptcha")
    public ShearCaptcha getShearCaptcha() {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(width, height);
        captcha.setBackground(background);
        captcha.setFont(font);
        return captcha;
    }

}
