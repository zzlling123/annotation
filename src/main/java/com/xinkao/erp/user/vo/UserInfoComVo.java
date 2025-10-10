package com.xinkao.erp.user.vo;


import lombok.Data;


@Data
public class UserInfoComVo {
	
    private String account;
    
    private String realName;
    
    private String avatar;
    
    private String mobile;
    
    
    private Integer isSuper;
    
    private Integer level;
}
