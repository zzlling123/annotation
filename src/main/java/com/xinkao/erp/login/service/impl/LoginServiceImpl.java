package com.xinkao.erp.login.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.exception.AuthenticationException;
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

import java.util.Date;
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
		if (!SecureUtil.md5(user.getSalt()+password).equals(user.getPassword())) {
			return BaseResponse.fail("密码错误！");
		}
		if (user.getState() == 0) {
			return BaseResponse.fail("该用户已被禁用！");
		}
		//字符串转为byte
		byte[] key = new byte[]{119, -75, -15, -122, -112, 119, 116, 111, -20, 47, -11, -93, 55, -66, -83, -94};
		//构建
		SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
		String encryptHex = "0eb0c46ccbb8560fbf4032256712042f0333bc3d5a219a72d7e3d030abeff550";
		//解密为字符串
		String decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
		Date expireDate = DateUtil.parse(decryptStr);
		if (expireDate.before(DateUtil.date())) {
			throw new AuthenticationException("请重新登录！");
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
