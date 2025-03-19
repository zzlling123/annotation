package com.xinkao.erp.login.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.xinkao.erp.common.util.ip.IpUtils;
import com.xinkao.erp.login.vo.UserLoginResultVo;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录通用服务
 * @author hys_thanks
 */
@Slf4j
@Service
public class LoginCommonServiceImpl {
	/**
	 * 统一获取登录相关信息
	 * @param username
	 * @param loginFlag
	 * @param msg
	 * @param request
	 * @return
	 */
	protected UserLoginResultVo crtLoginResult(String username, String loginFlag, String msg,
			HttpServletRequest request) {
		UserLoginResultVo resultVo = new UserLoginResultVo();
		resultVo.setUsername(username);
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
}
