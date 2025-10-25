package com.xinkao.erp.user.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.user.entity.Role;

import lombok.Data;


@Data
public class RoleDetailVo implements OutputConverter<RoleDetailVo, Role>{
    
	private String roleId;
    
	private String roleName;
	
	private Integer level;
    
    private Integer isDefault;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
    
    private String createBy="";
    
    private String createUser ="";
    
    private String remark;
}
