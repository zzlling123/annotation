package com.xinkao.erp.device.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceVO {
    
    private Long id;
    
    private String deviceName;

    private String userName;

    private Integer restartStatus;
    
    private String description;
    
    private String macAddress;
    
    private String deviceKey;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime keyGenerateTime;
    
    private Integer keyValidHours;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime keyExpireTime;
    
    private Integer status;
    
    private String statusDesc;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastActivateTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    private String createBy;
    
    private String updateBy;
    
    private Boolean keyValid;
} 