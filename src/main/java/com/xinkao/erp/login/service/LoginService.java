package com.xinkao.erp.login.service;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.login.param.ApLoginParam;
import com.xinkao.erp.login.vo.LoginUserVo;
import com.xinkao.erp.user.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录相关的服务
 **/
public interface LoginService {
	/**
	 * 账号密码登录
	 * @param apLoginParam
	 * @return
	 */
	BaseResponse<LoginUserVo> login(ApLoginParam apLoginParam, HttpServletRequest request);

	User getAccountByUserName(String username);
}
