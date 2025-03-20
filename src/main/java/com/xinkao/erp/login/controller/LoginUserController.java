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
/**
 * 登录用户信息相关服务
 **/
@RequestMapping("login")
@RestController
public class LoginUserController extends BaseController{
	@Resource
  	private LoginService loginService;

	@Autowired
	private SimpleCharVerifyCodeGenImpl verifyCodeGenService;


	/**
	 * @Description 获取验证码图片
	 * @Date 2020/12/21 10:30
	 * @Param [response]
	 * @return
	 */
	@ApiOperation("获取验证码")
	@GetMapping("/getVerificationCode")
	public void verifyCode(HttpServletResponse response) throws IOException {
		String uuid = RandomUtil.randomString(10);
		System.out.println("uuid：" + uuid);
		VerifyCodeDao verifyCode = verifyCodeGenService.generate(80, 28);
		String code = verifyCode.getCode();
		System.out.println("验证码：" + code);
		// 将验证码信息放到Redis缓存中
		redisUtil.set(uuid, code, 2, TimeUnit.MINUTES);
		//设置响应头
		response.setHeader("Pragma", "no-cache");
		//设置响应头
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Access-Control-Expose-Headers", "*");
		// 将uuid放入响应头，前端需要保存到浏览器缓存
		response.setHeader("uuid", uuid);
		//在代理服务器端防止缓冲
		response.setDateHeader("Expires", 0);
		//设置响应内容类型
		response.setContentType("image/jpeg");
		response.getOutputStream().write(verifyCode.getImgBytes());
		response.getOutputStream().flush();
	}

	/**
	 * 执行登录
	 *
	 * @return
	 */
	@PostMapping("/login")
	public BaseResponse<LoginUserVo> login(@RequestBody @Valid ApLoginParam apLoginParam, HttpServletRequest request) {
		return loginService.login(apLoginParam,request);
	}

	/**
	 * 退出登录
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/logout")
	@Log(content = "退出登录", operationType = OperationType.UPDATE, isSaveRequestData = false)
	public BaseResponse logout() {
		return redisUtil.deleteObject(HttpContextUtils.getHttpServletRequest().getHeader("Authorization"))? BaseResponse.ok("退出成功！"):BaseResponse.fail("失败");
	}
}
