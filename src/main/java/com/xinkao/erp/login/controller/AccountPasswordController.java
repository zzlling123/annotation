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

}
