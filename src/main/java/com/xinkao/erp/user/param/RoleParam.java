package com.xinkao.erp.user.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.user.entity.Role;

import lombok.Data;

import java.util.List;


@Data
public class RoleParam implements InputConverter<Role> {

	private String id;
	
	@NotEmpty(message = "角色名称不能为空")
	private String roleName;

	
	private String roleContent;

	
	private List<String> menuIds;
}
