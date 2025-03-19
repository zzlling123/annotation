package com.xinkao.erp.common.config.properties;

import static cn.hutool.core.text.CharSequenceUtil.addSuffixIfNot;
import static com.xinkao.erp.common.constant.XinKaoConstant.FILE_SEPARATOR;
import static com.xinkao.erp.common.constant.XinKaoConstant.USER_HOME;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.xinkao.erp.common.constant.XinKaoConstant;

import lombok.Data;

/**
 * 自定义参数配置
 **/
@Data
@ConfigurationProperties(prefix = "xinkao")
public class XinKaoProperties {

    /**
     * 本地文件默认存储路径
     */
    private String workDir = addSuffixIfNot(USER_HOME, FILE_SEPARATOR) + "." +XinKaoConstant.PROJECT_PREFIX + FILE_SEPARATOR;
    /**
     * 默认上送文件夹
     */
    private String uploadDir = "upload";
    /**
     * jwt配置
     */
    private JWTProperties token = new JWTProperties();
    /**
     * 验证码配置
     */
    private CaptchaProperties captcha = new CaptchaProperties();
    /**
     * 线程池配置
     */
    private ThreadPoolProperties threadPool = new ThreadPoolProperties();
}
