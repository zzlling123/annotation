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
    private String userName;

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
     * 角色
     */
    @NotBlank(message = "角色不能为空")
    private String roleId;
}
