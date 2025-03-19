package com.xinkao.erp.user.vo;

import lombok.Data;

/**
 * 角色实体
 * @author hys_thanks
 *
 */
@Data
public class RoleUserVo {
    /** 角色主键 **/
	private String roleId;
    /** 角色名称 **/
	private String roleName;
	/** 等级 **/
	private Integer level;
    /** 是否系统默认：0-系统默认 1-不是 */
    private Integer isDefault;
    /**
     * 是否已经勾选:0-未勾选 1-已勾选
     * **/
    private Integer isSelected = 0;
}
