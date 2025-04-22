package com.xinkao.erp.user.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * <p>
 * 用户修改密码参数
 * </p>
 */
@Data
public class AccountUpdatePwdParam {

	@NotBlank(message = "请输入新密码")
	@Size(max = 20,message = "新密码最多不能超过20字符")
	private String newPwd;

	@NotBlank(message = "请再次输入新密码")
	private String newPwdAgain;
	
	@NotBlank(message = "请输入原密码")
	private String oldPwd;
}
