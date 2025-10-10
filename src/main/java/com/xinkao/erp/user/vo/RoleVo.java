package com.xinkao.erp.user.vo;

import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.user.entity.Role;

import lombok.Data;


@Data
public class RoleVo implements OutputConverter<RoleVo, Role>{
    
	private String roleId;
    
	private String roleName;
	
	private Integer level;
    
    private Integer isDefault;
}
