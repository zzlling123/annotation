package com.xinkao.erp.user.vo;


import lombok.Data;

/**
 * 用户通用字段
 * @author hys_thanks
 */
@Data
public class UserInfoComVo {
	/**
     * 账号
     */
    private String account;
    /**
     * 昵称
     */
    private String realName;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 手机号
     */
    private String mobile;
    
    /**
     * 是否超级管理员:0-不是 1-是
     */
    private Integer isSuper;
    /**
     * 账号等级:0-学校账号 10-区县账号 20-市级账号
     */
    private Integer level;
}
