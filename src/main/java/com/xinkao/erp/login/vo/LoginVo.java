package com.xinkao.erp.login.vo;

import lombok.Data;

/**
 * 登录返回实体-vo
 * @author hys_thanks
 *
 */
@Data
public class LoginVo {
	
	/**用户token**/
	private String accessToken;
	
	/**密码强度状态: -1-密码较弱 0-正常 1-默认密码**/
	private Integer pwdStatus;
	
	/**密码强度状态**/
	private String pwdStatusStr;
}
