package com.xinkao.erp.user.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.util.PasswordCheckUtil;
import com.xinkao.erp.login.service.UserOptLogService;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.mapper.UserMapper;
import com.xinkao.erp.user.param.UserParam;
import com.xinkao.erp.user.param.UserUpdateParam;
import com.xinkao.erp.user.query.UserQuery;
import com.xinkao.erp.user.service.UserService;
import com.xinkao.erp.user.vo.UserPageVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.user.service.RoleService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

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

	/**
	 * 分页
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/page")
	@ApiOperation("分页")
	public BaseResponse<Page<UserPageVo>> page(@RequestBody UserQuery query) {
		//获取用户信息
		Pageable pageable = query.getPageInfo();
		Page<UserPageVo> voPage = userService.page(query, pageable);
		return BaseResponse.ok(voPage);
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
	 * 新增用户
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/save")
	@ApiOperation("新增用户")
	@Log(content = "新增用户", operationType = OperationType.INSERT, isSaveRequestData = false)
	public BaseResponse save(@Valid @RequestBody UserParam userParam) {
		//校验密码格式
		if (!PasswordCheckUtil.evalPassword(userParam.getPassword())) {
			return BaseResponse.fail("新密码须6-18位，包含字母和数字，不能连续，且必须含有大写和小写字母");
		}
		return userService.save(userParam);
	}

	/**
	 * 修改用户
	 *
	 * @return
	 */
	@PrimaryDataSource
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
	@PostMapping("/resetPassword")
	@ApiOperation("重置密码")
	public BaseResponse resetPassword(@RequestBody User user) {
		if (user.getId() == null){
			return BaseResponse.fail("重置密码,userId不可为空！");
		}
		return userService.resetPassword(user.getId());
	}
}
