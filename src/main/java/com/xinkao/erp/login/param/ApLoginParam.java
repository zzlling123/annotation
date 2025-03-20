package com.xinkao.erp.login.param;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 账号密码登录参数
 **/
@Data
public class ApLoginParam {

    @NotBlank(message = "请填写用户名")
    @ApiModelProperty("用户名")
    private String username;

    @NotBlank(message = "请填写密码")
    @ApiModelProperty("密码")
    private String password;

    @NotBlank(message = "请填写验证码")
    @ApiModelProperty("验证码")
    private String code;

    @NotBlank(message = "请填写uuid")
    @ApiModelProperty("uuid")
    private String uuid;
}
