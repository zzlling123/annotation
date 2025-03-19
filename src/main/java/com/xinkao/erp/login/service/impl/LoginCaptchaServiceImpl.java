package com.xinkao.erp.login.service.impl;

import java.awt.Color;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.xinkao.erp.common.config.properties.XinKaoProperties;
import com.xinkao.erp.common.exception.BusinessException;
import com.xinkao.erp.core.captcha.UnsignedMathGenerator;
import com.xinkao.erp.login.service.LoginCaptchaService;
import com.xinkao.erp.login.vo.GenCaptchaResultVo;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
/**
 * 验证码服务
 * @author hys_thanks
 */
@Service
public class LoginCaptchaServiceImpl implements LoginCaptchaService {
	// 圆圈干扰验证码
	@Resource(name = "CircleCaptcha")
	private CircleCaptcha circleCaptcha;
	// 线段干扰的验证码
	@Resource(name = "LineCaptcha")
	private LineCaptcha lineCaptcha;
	// 扭曲干扰验证码
	@Resource(name = "ShearCaptcha")
	private ShearCaptcha shearCaptcha;
	@Resource
	private XinKaoProperties xinKaoProperties;
	/**
	 * 生成手机验证码
	 */
	@Override
	public GenCaptchaResultVo genMobileCaptcha(String mobile) {
		GenCaptchaResultVo captchaResult = new GenCaptchaResultVo();
		String charList = xinKaoProperties.getCaptcha().getCharList();
		if(StringUtils.isBlank(charList)) {
			throw new BusinessException("请初始化验证码规则");
		}
		Integer charLength = xinKaoProperties.getCaptcha().getCharLength();
		charLength = (charLength == null)?4:charLength;
		if(charList.length() < charLength) {
			throw new BusinessException("请初始化验证码规则");
		}
		String code = RandomStringUtils.random(charLength, charList);
		captchaResult.setCode(code);
		captchaResult.setCodePic("");
		return captchaResult;
	}

	/**
	 * 生成图片验证码
	 */
	@Override
	public GenCaptchaResultVo genPicCaptcha() {
		GenCaptchaResultVo captchaResult = new GenCaptchaResultVo();
		// 生成验证码
		CodeGenerator codeGenerator;
		AbstractCaptcha captcha;
		String code = null;
		switch (xinKaoProperties.getCaptcha().getType()) {
		case "math":
			codeGenerator = new UnsignedMathGenerator(xinKaoProperties.getCaptcha().getNumberLength());
			break;
		case "char":
			codeGenerator = new RandomGenerator(xinKaoProperties.getCaptcha().getCharList(),
					xinKaoProperties.getCaptcha().getCharLength());
			break;
		default:
			throw new IllegalArgumentException("验证码类型异常");
		}
		switch (xinKaoProperties.getCaptcha().getCategory()) {
		case "line":
			captcha = lineCaptcha;
			break;
		case "circle":
			captcha = circleCaptcha;
			break;
		case "shear":
			captcha = shearCaptcha;
			break;
		default:
			throw new IllegalArgumentException("验证码类别异常");
		}
		captcha.setBackground(new Color(248, 248, 248));
		captcha.setGenerator(codeGenerator);
		captcha.createCode();
		if ("math".equals(xinKaoProperties.getCaptcha().getType())) {
			code = getCodeResult(captcha.getCode());
		} else if ("char".equals(xinKaoProperties.getCaptcha().getType())) {
			code = captcha.getCode();
		}
		captchaResult.setCode(code);
		captchaResult.setCodePic(captcha.getImageBase64());
		return captchaResult;
	}
	/**
	 * 数学计算方法获取code
	 * @param capStr
	 * @return
	 */
	private String getCodeResult(String capStr) {
		int numberLength = xinKaoProperties.getCaptcha().getNumberLength();
		int a = Convert.toInt(StrUtil.sub(capStr, 0, numberLength).trim());
		char operator = capStr.charAt(numberLength);
		int b = Convert.toInt(StrUtil.sub(capStr, numberLength + 1, numberLength + 1 + numberLength).trim());
		switch (operator) {
		case '*':
			return a * b + "";
		case '+':
			return a + b + "";
		case '-':
			return a - b + "";
		default:
			return "";
		}
	}

}
