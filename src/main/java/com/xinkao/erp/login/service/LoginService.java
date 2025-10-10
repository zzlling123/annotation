package com.xinkao.erp.login.service;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.login.param.ApLoginParam;
import com.xinkao.erp.login.param.RegisterParam;
import com.xinkao.erp.login.vo.LoginUserVo;
import com.xinkao.erp.user.entity.User;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {

	BaseResponse<?> register(RegisterParam registerParam);

	BaseResponse<LoginUserVo> login(ApLoginParam apLoginParam, HttpServletRequest request);

	User getAccountByUserName(String username);
}
