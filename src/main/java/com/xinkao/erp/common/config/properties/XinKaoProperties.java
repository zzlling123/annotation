package com.xinkao.erp.common.config.properties;

import static cn.hutool.core.text.CharSequenceUtil.addSuffixIfNot;
import static com.xinkao.erp.common.constant.XinKaoConstant.FILE_SEPARATOR;
import static com.xinkao.erp.common.constant.XinKaoConstant.USER_HOME;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.xinkao.erp.common.constant.XinKaoConstant;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "xinkao")
public class XinKaoProperties {

    private String workDir = addSuffixIfNot(USER_HOME, FILE_SEPARATOR) + "." +XinKaoConstant.PROJECT_PREFIX + FILE_SEPARATOR;
    private String uploadDir = "upload";
    private JWTProperties token = new JWTProperties();
    private CaptchaProperties captcha = new CaptchaProperties();
    private ThreadPoolProperties threadPool = new ThreadPoolProperties();
}
