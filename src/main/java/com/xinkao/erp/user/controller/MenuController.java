package com.xinkao.erp.user.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import cn.hutool.core.util.StrUtil;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.user.entity.Menu;
import com.xinkao.erp.user.param.MenuParam;
import com.xinkao.erp.user.service.MenuService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xinkao.erp.common.annotation.DataAuth;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.enums.busi.UserLevelEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.user.service.RoleService;
import com.xinkao.erp.user.vo.MenuRoleVo;

/**
 * 菜单相关
 * @author 777
 *
 */
@RequestMapping("/menu")
@RestController
public class MenuController extends BaseController {
	@Autowired
	private MenuService menuService;
	/**
	 * 根据用户权限获取用户菜单
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/getList")
	@ApiOperation("根据用户权限获取用户菜单")
	public BaseResponse<List<Menu>> getList() {
		return menuService.getList();
	}

	/**
	 * 新增菜单
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/save")
	@ApiOperation("新增菜单")
	@Log(content = "新增菜单",operationType = OperationType.INSERT)
	public BaseResponse save(@Valid @RequestBody MenuParam param) {
		return menuService.save(param);
	}

	/**
	 * 修改菜单
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/update")
	@ApiOperation("修改菜单")
	@Log(content = "修改菜单",operationType = OperationType.UPDATE)
	public BaseResponse update(@Valid @RequestBody MenuParam param) {
		return menuService.update(param);
	}

	/**
	 * 删除
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/del")
	@ApiOperation("删除菜单")
	@Log(content = "删除菜单",operationType = OperationType.UPDATE)
	public BaseResponse del(@RequestBody UpdateStateParam updateStateParam) {
		if (StrUtil.isBlank(updateStateParam.getIds())){
			return BaseResponse.fail("参数错误,id不可为空！");
		}
		return menuService.del(updateStateParam.getIds());
	}
}
