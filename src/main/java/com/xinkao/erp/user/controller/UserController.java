package com.xinkao.erp.user.controller;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.login.service.UserOptLogService;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.mapper.UserMapper;
import com.xinkao.erp.user.query.UserQuery;
import com.xinkao.erp.user.service.DingService;
import com.xinkao.erp.user.service.UserService;
import com.xinkao.erp.user.vo.UserPageVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.user.service.RoleService;

import lombok.extern.slf4j.Slf4j;

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
	private RoleService roleService;
	@Resource
	protected UserService userService;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private DingService dingService;
	@Autowired
	private UserOptLogService userOptLogService;


	/**
	 * 获取用户手机号
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/getMobileById")
	@ApiOperation("获取用户手机号")
	public BaseResponse getMobileById(@RequestBody User user) {
		return BaseResponse.ok("成功",userService.getById(user.getId()).getMobile());
	}

	/**
	 * 职务下拉列表
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/getDutiesList")
	@ApiOperation("职务下拉列表")
	public BaseResponse getDutiesList(@RequestBody UserQuery userQuery) {
		return BaseResponse.ok(userMapper.getDutiesList(userQuery));
	}

	/**
	 * 批量启用禁用
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/updateState")
	@ApiOperation("启用禁用")
	public BaseResponse updateState(@RequestBody UpdateStateParam updateStateParam) {
		if (updateStateParam.getIds() == null || updateStateParam.getIds().equals("")){
			return BaseResponse.fail("参数错误,id不可为空！");
		}
		return userService.updateState(updateStateParam);
	}

	/**
	 * 用户编辑
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/update")
	@ApiOperation("用户编辑")
	public BaseResponse updateState(@RequestBody User user) {
		if (user.getId() == null || user.getRoleId()==null){
			return BaseResponse.fail("参数错误,id和角色不可为空！");
		}
		User userOld = userService.getById(user.getId());
		userService.updateById(user);
		userOptLogService.saveLog("用户编辑,姓名："+userOld.getRealName(), JSON.toJSONString(user));
		return BaseResponse.ok("编辑成功");
	}

}
