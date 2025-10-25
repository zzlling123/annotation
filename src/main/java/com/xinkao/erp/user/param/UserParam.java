package com.xinkao.erp.user.param;

import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.user.entity.User;

import lombok.Data;


@Data
public class UserParam implements InputConverter<User>{
	
	private String userId;
	 
	@NotBlank(message = "账号不能为空")
	@Length(min = 3,max = 20,message = "账号长度最低为3位,最高为20位")
    private String username;

    
    private String password;
    
    @NotBlank(message = "姓名不能为空")
    private String realName;

    @ApiModelProperty("所属班级")
    private String classId;

    @ApiModelProperty("性别")
    private String sex;

    
    @NotBlank(message = "角色不能为空")
    private String roleId;

    @ApiModelProperty("身份证号")
    private String idCard;

    @ApiModelProperty("头像")
    private String headImg;

    @ApiModelProperty("手机号")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("职务")
    private String duty;
}
