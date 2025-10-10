package com.xinkao.erp.user.vo;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.user.entity.User;

import lombok.Data;

@Data
public class UserListVo implements OutputConverter<UserListVo, User>{
    
	private String userId;
    
    private List<RoleVo> roleList;
    
    private Integer isLocked;
    
    private String lockTime;
    
    private String lockCause;
    
    private String lastLoginIp;
    
    @TableField("last_login_time")
    private String lastLoginTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
}
