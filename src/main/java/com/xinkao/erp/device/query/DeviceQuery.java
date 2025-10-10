package com.xinkao.erp.device.query;

import lombok.Data;

@Data
public class DeviceQuery {
    
    private String deviceName;
    
    private String macAddress;
    
    private Integer status;
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;

    private String userName;

} 