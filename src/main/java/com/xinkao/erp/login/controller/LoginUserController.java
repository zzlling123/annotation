package com.xinkao.erp.login.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.util.RandomUtil;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.util.HttpContextUtils;
import com.xinkao.erp.login.mapper.VerifyCodeDao;
import com.xinkao.erp.login.param.ApLoginParam;
import com.xinkao.erp.login.param.RegisterParam;
import com.xinkao.erp.login.service.impl.SimpleCharVerifyCodeGenImpl;
import com.xinkao.erp.login.vo.LoginUserVo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.login.service.LoginService;
@RequestMapping("login")
@RestController
public class LoginUserController extends BaseController{
	@Resource
  	private LoginService loginService;

	@Autowired
	private SimpleCharVerifyCodeGenImpl verifyCodeGenService;

	@GetMapping("/getVerificationCode")
	public void verifyCode(HttpServletResponse response) throws IOException {
		String uuid = RandomUtil.randomString(10);
		VerifyCodeDao verifyCode = verifyCodeGenService.generate(80, 28);
		String code = verifyCode.getCode();

		redisUtil.set(uuid, code, 2, TimeUnit.MINUTES);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Access-Control-Expose-Headers", "*");
		response.setHeader("uuid", uuid);
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		response.getOutputStream().write(verifyCode.getImgBytes());
		response.getOutputStream().flush();
	}

	@PostMapping("/register")
	public BaseResponse<?> register(@RequestBody @Valid RegisterParam registerParam) {
		return loginService.register(registerParam);
	}

	@PostMapping("/login")
	public BaseResponse<LoginUserVo> login(@RequestBody @Valid ApLoginParam apLoginParam, HttpServletRequest request) {
		return loginService.login(apLoginParam,request);
	}

	@PrimaryDataSource
	@PostMapping("/logout")
	public BaseResponse logout() {
		return redisUtil.deleteObject(HttpContextUtils.getHttpServletRequest().getHeader("Authorization"))? BaseResponse.ok("退出成功！"):BaseResponse.fail("失败");
	}
}
