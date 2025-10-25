package com.xinkao.erp.login.param;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ApLoginParam {

    @NotBlank(message = "请填写用户名或手机号")
    @ApiModelProperty("用户名或手机号")
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
