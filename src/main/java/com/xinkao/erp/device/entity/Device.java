package com.xinkao.erp.device.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备实体类
 */
@Data
@TableName("device")
public class Device {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 设备名称
     */
    private String deviceName;
    
    /**
     * 设备描述
     */
    private String description;
    
    /**
     * MAC地址
     */
    private String macAddress;
    
    /**
     * 设备密钥
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
} 