package com.xinkao.erp.system.model.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;


@Data
public class SysConfigParam {

    
    private String id;

    
    @NotBlank(message = "参数名称不能为空")
    @Size(min = 0, max = 100, message = "参数名称不能超过100个字符")
    
    private String configName;

    
    @NotBlank(message = "参数键名长度不能为空")
    @Size(min = 0, max = 100, message = "参数键名长度不能超过100个字符")
    
    private String configKey;

    
    @NotBlank(message = "参数键值不能为空")
    @Size(min = 0, max = 500, message = "参数键值长度不能超过500个字符")
    
    private String configValue;

    
    private int system;

    
    private String remark;
}
