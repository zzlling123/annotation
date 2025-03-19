package com.xinkao.erp.user.vo;

import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.user.entity.Role;

import lombok.Data;

/**
 * 角色实体
 * @author hys_thanks
 *
 */
@Data
public class RoleVo implements OutputConverter<RoleVo, Role>{
    /** 角色主键 **/
	private String roleId;
    /** 角色名称 **/
	private String roleName;
	/***角色等级*/
	private Integer level;
    /** 是否系统默认：0-系统默认 1-不是 */
    private Integer isDefault;
}
