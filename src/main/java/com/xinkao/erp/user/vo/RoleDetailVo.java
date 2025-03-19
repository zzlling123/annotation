package com.xinkao.erp.user.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.user.entity.Role;

import lombok.Data;

/**
 * 角色实体
 * @author hys_thanks
 *
 */
@Data
public class RoleDetailVo implements OutputConverter<RoleDetailVo, Role>{
    /** 角色主键 **/
	private String roleId;
    /** 角色名称 **/
	private String roleName;
	/**
     * 等级:10-学校账号 20-区县账号 30-市级账号
     */
	private Integer level;
    /** 是否系统默认：0-系统默认 1-不是 */
    private Integer isDefault;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
    /**
     * 创建者主键
     */
    private String createBy="";
    /**
     * 创建者
     */
    private String createUser ="";
    /**
     * 备注
     */
    private String remark;
}
