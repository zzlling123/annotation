package com.xinkao.erp.common.config.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * 验证码属性
 **/
@Setter
@Getter
public class CaptchaProperties {

    /**
     * 验证码类型
     */
    private String type;

    /**
     * 验证码类别
     */
    private String category;
    
    /**
     * 随机码列表
     */
    private String charList;

    /**
     * 数字验证码位数
     */
    private Integer numberLength;

    /**
     * 字符验证码长度
     */
    private Integer charLength;
}
