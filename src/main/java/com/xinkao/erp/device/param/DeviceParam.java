package com.xinkao.erp.device.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DeviceParam {
    
    private Long id;
    
    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    @NotBlank(message = "用户名称不能为空")
    private String userName;
    
    private String description;
    
    @NotBlank(message = "MAC地址不能为空")
    private String macAddress;
    
    @NotNull(message = "密钥有效期不能为空，默认24小时")
    private Integer keyValidHours=24;

    private Integer status = 0;

    private int restartStatus;
} 