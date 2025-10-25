package com.xinkao.erp.user.param;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class PersonalInfoUpdateParam {

    @ApiModelProperty("真实姓名")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String realName;

    @ApiModelProperty("邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @ApiModelProperty("头像URL")
    private String headImg;
}
