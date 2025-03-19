package com.xinkao.erp.user.param;

import javax.validation.constraints.NotBlank;

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
    private String account;

    /**
     * 密码
     */
    private String password;
    /**
     * 昵称
     */
    @NotBlank(message = "姓名不能为空")
    private String realName;
    /**
     * 头像
     */
    private String avatar;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 所在区县主键
     */
    @NotBlank(message = "所属机构不能为空")
    private String officeId;
    /**
     * 所在学校主键
     */
    private String schoolId ="";

    /**
     * 是否超级管理员:0-不是 1-是
     */
    private Integer isSuper = 0;
    
    /**
     * 角色列表
     */
    @NotBlank(message = "请至少选择一个角色")
    private String roleIdListStr;
}
