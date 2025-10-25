package com.xinkao.erp.login.vo;

import lombok.Data;

@Data
public class LoginVo {

	private String accessToken;

	private Integer pwdStatus;

	private String pwdStatusStr;
}
