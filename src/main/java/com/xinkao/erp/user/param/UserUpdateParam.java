package com.xinkao.erp.user.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

/**
 * 用户修改参数
 **/
@Data
public class UserUpdateParam {
    /**
     * 用户名
     */
    @NotBlank(message = "姓名不能为空")
    private String realName;
    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String account;
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String mobile;
    /**
     * 头像
     */
    private String avatar;
}
