package com.xinkao.erp.common.config.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CaptchaProperties {

    private String type;

    private String category;
    
    private String charList;
    private Integer numberLength;
    private Integer charLength;
}
