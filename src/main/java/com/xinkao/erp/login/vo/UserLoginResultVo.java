package com.xinkao.erp.login.vo;

import lombok.Data;

/**
 * 用户登录结果vo
 * 记录日志使用
 * @author hys_thanks
 */
@Data
public class UserLoginResultVo {
	/**
	 * 登录用户名
	 */
	private String username;
	/**
	 * 登录人姓名
	 */
	private String realName;
	/**
	 * 登录成功标识
	 */
	private String loginFlag;
	/**
	 * 操作系统
	 */
	private String os;
	/**
	 * 访问ip
	 */
	private String ip;
	/**
	 * 访问浏览器
	 */
	private String browser;
	/**
	 * 提示信息
	 */
	private String msg;
}
