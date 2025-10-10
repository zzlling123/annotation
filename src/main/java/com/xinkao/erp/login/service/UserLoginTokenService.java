package com.xinkao.erp.login.service;

import javax.servlet.http.HttpServletRequest;

import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.login.entity.UserLoginToken;

public interface UserLoginTokenService extends BaseService<UserLoginToken> {
    LoginUser getLoginUser(HttpServletRequest request);




    void setLoginUser(LoginUser loginUser);




    void delLoginUser(String token);







    String createToken(LoginUser loginUser);







    void verifyToken(LoginUser loginUser);







    String getUsernameFromToken(String token);
}
