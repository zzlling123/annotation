package com.xinkao.erp.device.query;

import lombok.Data;

/**
 * 设备查询参数
 */
@Data
public class DeviceQuery {
    
    /**
     * 设备名称（模糊查询）
     */
    private String deviceName;
    
    /**
     * MAC地址（模糊查询）
     */
    private String macAddress;
    
    /**
     * 设备状态
     */
    private Integer status;
    
    /**
     * 当前页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
} 