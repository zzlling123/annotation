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
import com.xinkao.erp.login.service.impl.SimpleCharVerifyCodeGenImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.login.service.LoginService;
/**
 * 登录用户信息相关服务
 **/
@RequestMapping("login")
@RestController
public class LoginUserController extends BaseController{
	@Resource
  	private LoginService loginService;
	@Value("${ding.CORPID}")
	private String CORPID;


	/**
	 * 获取CorpId
	 *
	 * @return
	 */
	@PostMapping("/getCorpId")
	public BaseResponse getCorpId() {
		return BaseResponse.ok("成功",CORPID);
	}

	/**
	 * 执行登录
	 *
	 * @return
	 */
	@PostMapping("/login")
	public BaseResponse login(@Valid @RequestBody ApLoginParam apLoginParam, HttpServletRequest request) {
		return loginService.login(apLoginParam,request);
	}

	/**
	 * 退出登录
	 *
	 * @return
	 */
	@PostMapping("/logout")
	@Log(content = "退出登录", operationType = OperationType.UPDATE, isSaveRequestData = false)
	public BaseResponse logout() {
		return redisUtil.deleteObject(HttpContextUtils.getHttpServletRequest().getHeader("Authorization"))? BaseResponse.ok("退出成功！"):BaseResponse.fail("失败");
	}
}
