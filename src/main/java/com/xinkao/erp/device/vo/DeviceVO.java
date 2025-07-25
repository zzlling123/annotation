package com.xinkao.erp.device.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备VO类
 */
@Data
public class DeviceVO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 重启状态：0-未重启，1-已重启
     */
    private Integer restartStatus;
    
    /**
     * 设备描述
     */
    private String description;
    
    /**
     * MAC地址
     */
    private String macAddress;
    
    /**
     * 设备密钥（部分隐藏）
     */
    private String deviceKey;
    
    /**
     * 密钥生成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime keyGenerateTime;
    
    /**
     * 密钥有效期（小时）
     */
    private Integer keyValidHours;
    
    /**
     * 密钥过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime keyExpireTime;
    
    /**
     * 设备状态：0-未激活，1-已激活，2-已禁用
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 最后激活时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastActivateTime;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    /**
     * 创建人
     */
    private String createBy;
    
    /**
     * 更新人
     */
    private String updateBy;
    
    /**
     * 密钥是否有效
     */
    private Boolean keyValid;
} 