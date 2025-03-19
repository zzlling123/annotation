package com.xinkao.erp.user.query;

import com.xinkao.erp.common.model.BasePageQuery;

import lombok.Getter;
import lombok.Setter;

/**
 * 管理端用户查询
 * @author hys_thanks
 */
@Setter
@Getter
public class UserPageQuery extends BasePageQuery {
	/**
	 * 账号
	 * **/
	private String account;
    /**
     * 姓名
     * **/
    private String realName;
    /**
     * 手机号
     * **/
    private String mobile;
	/**
	 * 查询的区县主键
	 */
	private String officeId;
	/**
	 * 查询的学校主键
	 */
	private String schoolId;
	/**
	 * 查询的角色主键
	 */
	private String roleId;

}