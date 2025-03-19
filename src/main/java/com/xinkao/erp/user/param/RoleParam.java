package com.xinkao.erp.user.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.user.entity.Role;

import lombok.Data;

import java.util.List;

/**
 * 角色新增或更新实体
 * @author hys_thanks
 */
@Data
public class RoleParam implements InputConverter<Role> {

	//修改时有值
	private String id;
	/**
	 * 角色名称
	 */
	@NotEmpty(message = "角色名称不能为空")
	private String roleName;

	/**
	 * 角色描述
	 */
	private String roleContent;

	/**
	 * 权限设置
	 */
	private List<String> menuIds;
}
