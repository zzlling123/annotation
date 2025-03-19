package com.xinkao.erp.login.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 管理端-用户登录表
 * </p>
 *
 * @author hanhys
 * @since 2023-08-03 14:18:36
 */
@Getter
@Setter
@TableName("sys_user_login_log")
public class UserLoginLog extends BaseEntity {
	   /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户名
     */
    @TableField("account")
    private String account;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 结果状态
     */
    @TableField("status")
    private Integer status;

    /**
     * 登录时间
     */
    @TableField("login_time")
    private String loginTime;

    /**
     * 登录IP地址
     */
    @TableField("ip_addr")
    private String ipAddr;

    /**
     * 登录地点
     */
    @TableField("login_location")
    private String loginLocation;

    /**
     * browser
     */
    @TableField("browser")
    private String browser;

    /**
     * os操作系统
     */
    @TableField("os")
    private String os;

    /**
     * 返回数据
     */
    @TableField("msg")
    private String msg;


}
