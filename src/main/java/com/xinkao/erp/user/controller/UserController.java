package com.xinkao.erp.user.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.DataScope;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.exception.BusinessException;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.param.ErrorImportTokenParam;
import com.xinkao.erp.common.util.ExcelUtils;
import com.xinkao.erp.common.util.PasswordCheckUtil;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.excel.UserImportErrorModel;
import com.xinkao.erp.user.excel.UserImportModel;
import com.xinkao.erp.user.excel.UserModelListener;
import com.xinkao.erp.user.param.AccountUpdatePwdParam;
import com.xinkao.erp.user.param.PersonalInfoUpdateParam;
import com.xinkao.erp.user.param.UserParam;
import com.xinkao.erp.user.param.UserUpdateParam;
import com.xinkao.erp.user.query.ExamAndPracticeBarQuery;
import com.xinkao.erp.user.query.UserQuery;
import com.xinkao.erp.user.service.UserService;
import com.xinkao.erp.user.vo.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.model.BaseResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 管理端用户相关服务
 * 
 * @author hys_thanks
 *
 */
@RequestMapping("/user")
@RestController
@Slf4j
public class UserController extends BaseController {
	@Resource
	protected UserService userService;
	@Autowired
	private RedisUtil redisUtils;
	
	@Value("${path.fileUrl}")
	private String fileUrl;
	@Value("${ipurl.url}")
	private String ipurl;

	/**
	 * 分页
	 *
	 * @return
	 */
	@PrimaryDataSource
	@DataScope(role = "1,2,18,19")
	@PostMapping("/page")
	@ApiOperation("分页")
	public BaseResponse<Page<UserPageVo>> page(@RequestBody UserQuery query) {
		//获取用户信息
		Pageable pageable = query.getPageInfo();
		Page<UserPageVo> voPage = userService.page(query, pageable);
		return BaseResponse.ok(voPage);
	}

	/**
	 * 用户导出
	 * @param response
	 * @param query
	 */
	@PrimaryDataSource
	@DataScope(role = "1,18,19")
	@ApiOperation(value = "用户导出")
	@RequestMapping(value = "/exportUser", method = RequestMethod.POST, produces = "application/octet-stream")
	public void exportUser(HttpServletResponse response,@RequestBody UserQuery query) {
		Pageable pageable = new Pageable();
		pageable.setPage(1);
		pageable.setPageSize(9999999);
		Page<UserPageVo> voPage = userService.page(query, pageable);
		List<UserPageVo> voPageList = voPage.getRecords();
		for (UserPageVo userPageVo : voPageList) {
			userPageVo.setSex("1".equals(userPageVo.getSex()) ? "男" : "女");
		}
		List<UserImportModel> list = BeanUtil.copyToList(voPage.getRecords(), UserImportModel.class);
		try {
			ExcelUtils.writeExcel(response, list, "用户模板", "用户模板",
					UserImportModel.class);
		} catch (IOException e) {
			throw new BusinessException("导出用户模板失败");
		}
	}

	/**
	 * 下拉列表教师信息
	 *
	 */
	@PrimaryDataSource
	@PostMapping("/getList")
	@ApiOperation("下拉列表教师信息")
	public BaseResponse<List<UserPageVo>> getList() {
		//查询所有教师角色用户
		List<User> userList = userService.lambdaQuery().eq(User::getIsDel, CommonEnum.IS_DEL.NO.getCode()).eq(User::getRoleId, 2).list();
		List<UserPageVo> userPageVoList = BeanUtil.copyToList(userList, UserPageVo.class);
		return BaseResponse.ok("成功",userPageVoList);
	}

	/**
	 * 获取专家列表信息
	 *
	 */
	@PrimaryDataSource
	@PostMapping("/getExpertList")
	@ApiOperation("获取专家列表信息")
	public BaseResponse<List<UserPageVo>> getExpertList() {
		//查询所有专家角色用户
		List<User> userList = userService.lambdaQuery().eq(User::getIsDel, CommonEnum.IS_DEL.NO.getCode()).eq(User::getRoleId, 20).list();
		List<UserPageVo> userPageVoList = BeanUtil.copyToList(userList, UserPageVo.class);
		return BaseResponse.ok("成功",userPageVoList);
	}

	/**
	 * 新增用户
	 *
	 * @return
	 */
	@PrimaryDataSource
	@DataScope(role = "1,18,19")
	@PostMapping("/save")
	@ApiOperation("新增用户")
	@Log(content = "新增用户", operationType = OperationType.INSERT, isSaveRequestData = false)
	public BaseResponse save(@Valid @RequestBody UserParam userParam) {
		//校验密码格式
//		if (!PasswordCheckUtil.evalPassword(userParam.getPassword())) {
//			return BaseResponse.fail("新密码须6-18位，包含字母和数字，不能连续，且必须含有大写和小写字母");
//		}
		return userService.save(userParam);
	}

	/**
	 * 修改用户
	 *
	 * @return
	 */
	@PrimaryDataSource
	@DataScope(role = "1")
	@PostMapping("/update")
	@ApiOperation("修改用户")
	public BaseResponse update(@Valid @RequestBody UserUpdateParam userUpdateParam) {
		return userService.update(userUpdateParam);
	}

	/**
	 * 删除
	 *
	 * @return
	 */
	@PrimaryDataSource
	@DataScope(role = "1")
	@PostMapping("/del")
	@ApiOperation("删除用户")
	public BaseResponse del(@RequestBody UpdateStateParam updateStateParam) {

		if (StrUtil.isBlank(updateStateParam.getIds())){
			return BaseResponse.fail("参数错误,id不可为空！");
		}
		return userService.del(updateStateParam.getIds());
	}

	/**
	 * 修改状态
	 *
	 * @return
	 */
	@PrimaryDataSource
	@DataScope(role = "1")
	@PostMapping("/updateState")
	@ApiOperation("修改状态")
	public BaseResponse updateState(@Valid @RequestBody UpdateStateParam updateStateParam) {
		return userService.updateState(updateStateParam);
	}

	/**
	 * 重置密码
	 *
	 * @return
	 */
	@PrimaryDataSource
	@DataScope(role = "1")
	@PostMapping("/resetPassword")
	@ApiOperation("重置密码")
	public BaseResponse resetPassword(@RequestBody User user) {
		if (user.getId() == null){
			return BaseResponse.fail("重置密码,userId不可为空！");
		}
		return userService.resetPassword(user.getId());
	}

	/**
	 * 导入用户
	 * @param file
	 * @return
	 */
	@PrimaryDataSource
	@DataScope(role = "1,18,19")
	@ApiOperation(value = "导入用户")
	@RequestMapping(value = "/importUpdateClass", method = RequestMethod.POST)
	public BaseResponse importUpdateClass(HttpServletResponse response, @RequestParam(value="file") MultipartFile file) {
		String token = RandomUtil.randomString(20);
		redisUtils.set(token, "", 2, TimeUnit.HOURS);
		UserModelListener userModelListener =  new UserModelListener(response,token);
		try {
			EasyExcel.read(file.getInputStream(), UserImportModel.class, userModelListener).sheet().doRead();
		} catch (IOException e) {
			throw new BusinessException("导入用户失败");
		}
		BaseResponse baseResponse = BeanUtil.copyProperties(JSON.parseObject(redisUtils.get(token)),BaseResponse.class);
		if ("ok".equals(baseResponse.getState())){
			return BaseResponse.ok(baseResponse.getMsg());
		}else{
			return BaseResponse.other(token);
		}
	}

	/**
	 * 下载错误用户导入文件
	 * @return
	 */
	@PrimaryDataSource
	@ApiOperation(value = "下载错误用户导入名单")
	@RequestMapping(value = "/getErrorUpdateClassImportExcel", method = RequestMethod.POST)
	public void getErrorUpdateClassImportExcel(HttpServletResponse response,@RequestBody ErrorImportTokenParam param) {
		JSONArray json = JSON.parseObject(redisUtils.get(param.getToken())).getJSONArray("data");
		List<UserImportErrorModel> stuUpdateClassImportErrorModels = BeanUtil.copyToList(json, UserImportErrorModel.class);
		//下载文件
		try {
			ExcelUtils.writeExcel(response, stuUpdateClassImportErrorModels, "错误用户文件", "错误用户",
					UserImportErrorModel.class);
		} catch (IOException e) {
			throw new BusinessException("导出错误用户导入文件失败");
		}
	}


	//-------------------------------------------------------个人中心------------------------------------------------

	/**
	 * 获取登录用户信息
	 * @return
	 */
	@PrimaryDataSource
	@DataScope(role = "1,2,3,18,19")
	@PostMapping("/getUserInfoBySelf")
	@ApiOperation("获取用户信息")
	public BaseResponse<UserInfoVo> getUserInfoBySelf() {
		return BaseResponse.ok(userService.getUserInfoBySelf());
	}

	/**
	 * 修改密码
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/updatePassword")
	@ApiOperation("修改密码")
	public BaseResponse<?> updatePassword(@RequestBody @Valid AccountUpdatePwdParam param, HttpServletRequest request) {
		BaseResponse<?> result = userService.updatePassword(param);
		
		// 如果密码修改成功，清除当前用户的token
		if (result != null && "ok".equals(result.getState())) {
			try {
				String token = request.getHeader("Authorization");
				if (StrUtil.isNotBlank(token)) {
					redisUtil.deleteObject(token);
					log.info("用户密码修改成功，已清除token");
				}
			} catch (Exception e) {
				log.warn("清除token失败: {}", e.getMessage());
			}
		}
		
		return result;
	}

	/**
	 * 获取练习/考试柱状图，计算练习，考试下各个题型分类type下的得分率
	 * @return
	 */
	@PrimaryDataSource
	@DataScope(role = "3")
	@PostMapping("/getExamAndPracticeBar")
	@ApiOperation("获取练习/考试柱状图，计算练习，考试下各个题型分类type下的得分率")
	public BaseResponse<List<ExamAndPracticeBarVo>> getExamAndPracticeBar(@RequestBody ExamAndPracticeBarQuery query) {
		return userService.getExamAndPracticeBar(query);
	}

	/**
	 * 获取练习/考试饼状图，计算练习，考试下所有题在各题型中的占比
	 * @return
	 */
	@PrimaryDataSource
	@DataScope(role = "3")
	@PostMapping("/getExamAndPracticePie")
	@ApiOperation("获取练习/考试饼状图，计算练习，考试下所有题在各题型中的占比")
	public BaseResponse<List<ExamAndPracticePieAllVo>> getExamAndPracticePie(@RequestBody ExamAndPracticeBarQuery query) {
		return userService.getExamAndPracticePie(query);
	}

	/**
	 * 更新个人信息
	 * @param param 个人信息更新参数
	 * @return 更新结果
	 */
	@PrimaryDataSource
	@PostMapping("/updatePersonalInfo")
	@ApiOperation("更新个人信息")
	public BaseResponse<?> updatePersonalInfo(@RequestBody @Valid PersonalInfoUpdateParam param) {
		return userService.updatePersonalInfo(param);
	}

	/**
	 * 头像上传
	 * @param file 头像文件
	 * @return 头像URL
	 */
	@PrimaryDataSource
	@PostMapping("/uploadAvatar")
	@ApiOperation("头像上传")
	public BaseResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
		try {
			// 验证文件类型
			String originalFilename = file.getOriginalFilename();
			if (originalFilename == null) {
				return BaseResponse.fail("文件名不能为空");
			}
			
			String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
			if (!extension.matches("\\.(jpg|jpeg|png|gif|bmp)$")) {
				return BaseResponse.fail("只支持图片格式：jpg、jpeg、png、gif、bmp");
			}
			
			// 验证文件大小（限制2MB）
			if (file.getSize() > 2 * 1024 * 1024) {
				return BaseResponse.fail("头像文件大小不能超过2MB");
			}
			
			// 创建文件目录
			File dir = new File(fileUrl);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			// 生成唯一文件名
			String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
			File targetFile = new File(fileUrl, fileName);
			
			// 保存文件
			file.transferTo(targetFile);
			
			// 返回访问URL
			String avatarUrl = ipurl + "/annotation/fileUrl/" + fileName;
			
			log.info("用户头像上传成功：{}", avatarUrl);
			return BaseResponse.ok("头像上传成功", avatarUrl);
			
		} catch (IOException e) {
			log.error("头像上传失败", e);
			return BaseResponse.fail("头像上传失败：" + e.getMessage());
		}
	}
}
