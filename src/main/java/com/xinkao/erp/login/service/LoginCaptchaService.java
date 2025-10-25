package com.xinkao.erp.login.service;

import com.xinkao.erp.login.vo.GenCaptchaResultVo;

public interface LoginCaptchaService {
	GenCaptchaResultVo genMobileCaptcha(String mobile);
	GenCaptchaResultVo genPicCaptcha();
}
