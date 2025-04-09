package com.xinkao.erp.login.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.util.ip.IpRegionUtils;
import com.xinkao.erp.common.util.ip.IpUtils;
import com.xinkao.erp.login.vo.LoginUserVo;
import com.xinkao.erp.user.service.UserService;
import org.springframework.stereotype.Service;

import com.xinkao.erp.common.constant.XinKaoConstant;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.common.util.ServletUtils;
import com.xinkao.erp.login.param.ApLoginParam;
import com.xinkao.erp.login.service.LoginService;
import com.xinkao.erp.login.vo.UserLoginResultVo;
import com.xinkao.erp.system.service.AsyncService;
import com.xinkao.erp.user.entity.User;

import java.util.concurrent.TimeUnit;

/**
 * 登录相关的服务具体实现
 **/
@Service
public class LoginServiceImpl extends LoginCommonServiceImpl implements LoginService {

	@Resource
	private AsyncService asyncService;
	@Resource
	private UserService userService;
	@Resource
	private RedisUtil redisUtil;

	@Override
	public BaseResponse<LoginUserVo> login(ApLoginParam loginParam, HttpServletRequest request) {
		String userCode = redisUtil.get(loginParam.getUuid());
		System.out.println("Redis缓存中的code：" + userCode);
		if (!StrUtil.equalsIgnoreCase(loginParam.getCode(), userCode)){
//			return BaseResponse.fail("验证码不正确！");
		}
		String username = loginParam.getUsername().trim();
		String password = loginParam.getPassword().trim();

		// 用户名不存在
		User user = getAccountByUserName(username);
		if (user == null) {
			return BaseResponse.fail("用户名不存在！");
		}
		// 密码错误
		System.out.println(SecureUtil.md5(user.getSalt()+password));
		if (!SecureUtil.md5(user.getSalt()+password).equals(user.getPassword())) {
			return BaseResponse.fail("密码错误！");
		}
		if (user.getState() == 0) {
			return BaseResponse.fail("该用户已被禁用！");
		}
		// 生成登录信息
		LoginUser loginUser = new LoginUser();
		String token = RandomUtil.randomString(20); // 随机生成唯一token
		// 记录登录日志
		UserLoginResultVo resultVo = crtLoginResult(username,user.getRealName(), XinKaoConstant.LOGIN_SUCCESS,"登录成功",request);
		asyncService.recordLogininfo(resultVo);
		loginUser.setUser(user);
		loginUser.setToken(token);
		loginUser.setLoginTs(System.currentTimeMillis());
		setUserAgent(loginUser);

		//存储用户登录信息
		redisUtil.set(token, loginUser, 24, TimeUnit.HOURS);
		LoginUserVo vo = BeanUtil.copyProperties(user, LoginUserVo.class);
		vo.setToken(token);
		return BaseResponse.ok("登录成功！", vo);
	}

	@Override
	public User getAccountByUserName(String username){
		return userService.lambdaQuery().eq(User::getUsername, username).eq(User::getIsDel, CommonEnum.IS_DEL.NO.getCode()).last("limit 1").one();
	}

	/**
	 * 统一获取登录相关信息
	 * @param username
	 * @param loginFlag
	 * @param msg
	 * @param request
	 * @return
	 */
	protected UserLoginResultVo crtLoginResult(String username,String realName, String loginFlag, String msg,
											   HttpServletRequest request) {
		UserLoginResultVo resultVo = new UserLoginResultVo();
		resultVo.setUsername(username);
		resultVo.setRealName(realName);
		resultVo.setLoginFlag(loginFlag);
		resultVo.setMsg(msg);
		final String ip = IpUtils.getIpAddr(request);
		resultVo.setIp(ip);
		final UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));
		// 获取客户端操作系统
		String os = userAgent.getOs().getName();
		// 获取客户端浏览器
		String browser = userAgent.getBrowser().getName();
		resultVo.setOs(os);
		resultVo.setBrowser(browser);
		return resultVo;
	}

	/**
	 * 设置用户代理信息
	 *
	 * @param loginUser 登录信息
	 */
	private void setUserAgent(LoginUser loginUser) {
		UserAgent userAgent = UserAgentUtil.parse(ServletUtils.getRequest().getHeader("User-Agent"));
		String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
		loginUser.setIpAddr(ip);
		loginUser.setLoginLocation(IpRegionUtils.getRegion(ip));
		loginUser.setBrowser(userAgent.getBrowser().getName());
		loginUser.setOs(userAgent.getOs().getName());
	}

}
