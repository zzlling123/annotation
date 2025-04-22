package com.xinkao.erp.user.param;

import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.user.entity.User;

import lombok.Data;

/**
 * 用户相关参数
 * @author hys_thanks
 */
@Data
public class UserParam implements InputConverter<User>{
	/**
	 * 用户主键
	 */
	private String userId;
	 /**
     * 账号
     */
	@NotBlank(message = "账号不能为空")
	@Length(min = 6,max = 50,message = "账号长度最低为6位,最高为50位")
    private String username;

    /**
     * 密码
     */
    private String password;
    /**
     * 昵称
     */
    @NotBlank(message = "姓名不能为空")
    private String realName;

    @ApiModelProperty("所属班级")
    private String classId;

    @ApiModelProperty("性别")
    private String sex;

    /**
     * 角色
     */
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
}
