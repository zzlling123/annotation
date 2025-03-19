package com.xinkao.erp.login.controller;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xinkao.erp.common.constant.XinKaoConstant;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.login.param.ApLoginParam;
import com.xinkao.erp.login.service.LoginCaptchaService;
import com.xinkao.erp.login.service.LoginService;
import com.xinkao.erp.login.vo.CaptchaResultVo;
import com.xinkao.erp.login.vo.GenCaptchaResultVo;
import com.xinkao.erp.login.vo.LoginVo;
import com.xinkao.erp.system.service.SysConfigService;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 验证码相关的管理
 * @author hys_thanks
 */
@Slf4j
@RequestMapping("/ap")
@RestController
public class AccountPasswordController extends BaseController{
   
//    @Resource
//    private SysConfigService sysConfigService;
//    @Resource
//	private LoginService loginService;
//    @Resource
//    private LoginCaptchaService captchaService;
//    /**
//     * 生成验证码
//     */
//    @GetMapping("/captchaImage")
//    public CaptchaResultVo getCode() {
//        CaptchaResultVo resultVo = new CaptchaResultVo();
//        //查看账号密码的验证码功能是否开放
//        boolean captchaPicOnOff = sysConfigService.getCaptchaPicOnOff();
//        resultVo.setCaptchaOnOff(String.valueOf(captchaPicOnOff));
//        if (!captchaPicOnOff) {
//        	resultVo.setCaptchaId("");
//        	resultVo.setImg("");
//            return resultVo;
//        }
//        //生成验证码
//        GenCaptchaResultVo captchaResult = captchaService.genPicCaptcha();
//        boolean captchaLowercaseOnOff = sysConfigService.getCaptchaLowercaseOnOff();
//        String code = captchaResult.getCode();
//        String codePic = captchaResult.getCodePic();
//        // 保存验证码信息
//        String uuid = IdUtil.simpleUUID();
//        String verifyKey = XinKaoConstant.CAPTCHA_CODE_KEY + uuid;
//        //判断大小写是否敏感
//        if(captchaLowercaseOnOff) {
//        	code = code.toLowerCase();
//        }
//        log.debug("生成的pic验证码是:{}",code);
//        redisUtil.set(verifyKey, code, XinKaoConstant.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
//        resultVo.setCaptchaId(uuid);
//    	resultVo.setImg(codePic);
//        return resultVo;
//    }
//   	/**
//   	 * 登录接口
//   	 */
//   	@PostMapping("/login")
//   	public BaseResponse login(ApLoginParam loginParam, HttpServletRequest request) {
//   		return loginService.login(loginParam,request);
//   	}
}
