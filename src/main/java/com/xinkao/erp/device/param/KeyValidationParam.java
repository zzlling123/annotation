package com.xinkao.erp.device.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 密钥验证参数
 */
@Data
public class KeyValidationParam {
    
    /**
     * MAC地址
     */
    @NotBlank(message = "MAC地址不能为空")
    private String macAddress;
    
    /**
     * 设备密钥
     */
    @NotBlank(message = "设备密钥不能为空")
    private String deviceKey;
} 