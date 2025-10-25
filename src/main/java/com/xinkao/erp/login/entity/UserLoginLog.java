package com.xinkao.erp.login.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_user_login_log")
public class UserLoginLog extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    @TableField("account")
    private String account;



    @TableField("real_name")
    private String realName;



    @TableField("status")
    private Integer status;



    @TableField("login_time")
    private String loginTime;



    @TableField("ip_addr")
    private String ipAddr;



    @TableField("login_location")
    private String loginLocation;



    @TableField("browser")
    private String browser;



    @TableField("os")
    private String os;



    @TableField("msg")
    private String msg;


}
