package com.xinkao.erp.device.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 设备参数类
 */
@Data
public class DeviceParam {
    
    /**
     * 设备ID（更新时使用）
     */
    private Long id;
    
    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    /**
     * 设备名称
     */
    @NotBlank(message = "用户名称不能为空")
    private String userName;
    
    /**
     * 设备描述
     */
    private String description;
    
    /**
     * MAC地址
     */
    @NotBlank(message = "MAC地址不能为空")
    private String macAddress;
    
    /**
     * 密钥有效期（小时）
     */
    @NotNull(message = "密钥有效期不能为空，默认24小时")
    private Integer keyValidHours = 24; // 默认24小时
    
    /**
     * 设备状态：0-未激活，1-已激活，2-已禁用
     */
    private Integer status = 0; // 默认未激活

    /**
     * 重启状态：0-未重启，1-已重启
     */
    private int restartStatus;
} 