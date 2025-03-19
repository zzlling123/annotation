package com.xinkao.erp.login.service;

import com.xinkao.erp.login.vo.GenCaptchaResultVo;

/**
 * 生成验证码相关的服务
 * @author hys_thanks
 */
public interface LoginCaptchaService {
	/**
	 * 生成手机验证码
	 * @param mobile
	 * @return
	 */
	GenCaptchaResultVo genMobileCaptcha(String mobile);
	/**
	 * 生成图片验证码
	 * @return
	 */
	GenCaptchaResultVo genPicCaptcha();
}
