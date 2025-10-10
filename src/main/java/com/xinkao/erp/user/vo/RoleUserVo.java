package com.xinkao.erp.user.vo;

import lombok.Data;


@Data
public class RoleUserVo {
    
	private String roleId;
    
	private String roleName;
	
	private Integer level;
    
    private Integer isDefault;
    
    private Integer isSelected = 0;
}
