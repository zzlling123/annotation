package com.xinkao.erp.login.vo;

import lombok.Data;

@Data
public class UserLoginResultVo {
	private String username;

	private String realName;

	private String loginFlag;

	private String os;

	private String ip;

	private String browser;

	private String msg;
}
