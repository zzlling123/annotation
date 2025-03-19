package com.xinkao.erp.login.service;

import javax.servlet.http.HttpServletRequest;

import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.login.entity.UserLoginToken;

/**
 * <p>
 * 管理端-登录token(定时删除) 服务类
 * </p>
 *
 * @author hanhys
 * @since 2023-08-03 14:18:36
 */
public interface UserLoginTokenService extends BaseService<UserLoginToken> {
	/**
     * 获取用户身份信息
     *
     * @return 用户信息
     * @param request
     */
    LoginUser getLoginUser(HttpServletRequest request);

    /**
     * 设置用户身份信息
     */
    void setLoginUser(LoginUser loginUser);

    /**
     * 删除用户身份信息
     */
    void delLoginUser(String token);

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    String createToken(LoginUser loginUser);

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    void verifyToken(LoginUser loginUser);

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    String getUsernameFromToken(String token);
}
