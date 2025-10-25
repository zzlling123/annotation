package com.xinkao.erp.user.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class UserUpdateParam {

    
    @NotBlank(message = "id不能为空")
    private String id;

    
    @NotBlank(message = "姓名不能为空")
    private String realName;

    
    @NotBlank(message = "账号不能为空")
    private String username;

    
    @NotBlank(message = "角色不能为空")
    private String roleId;

    @ApiModelProperty("身份证号")
    private String idCard;

    @ApiModelProperty("头像")
    private String headImg;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("职务")
    private String duty;

    @ApiModelProperty("所属班级")
    private String classId;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("状态")
    private String state;
}
