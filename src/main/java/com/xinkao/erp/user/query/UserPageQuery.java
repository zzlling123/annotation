package com.xinkao.erp.user.query;

import com.xinkao.erp.common.model.BasePageQuery;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class UserPageQuery extends BasePageQuery {
	
	private String account;
    
    private String realName;
    
    private String mobile;
	
	private String officeId;
	
	private String schoolId;
	
	private String roleId;

}