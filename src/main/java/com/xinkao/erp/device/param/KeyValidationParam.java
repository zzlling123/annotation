package com.xinkao.erp.device.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class KeyValidationParam {
    
    @NotBlank(message = "MAC地址不能为空")
    private String macAddress;
    
    @NotBlank(message = "设备密钥不能为空")
    private String deviceKey;
} 