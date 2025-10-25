package com.xinkao.erp.user.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import cn.hutool.core.util.StrUtil;
import com.xinkao.erp.common.annotation.DataScope;
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

@RequestMapping("/menu")
@RestController
public class MenuController extends BaseController {
	@Autowired
	private MenuService menuService;
	@PrimaryDataSource
	@PostMapping("/getList")
	public BaseResponse<List<Menu>> getList() {
		return menuService.getList();
	}

	@PrimaryDataSource
	@PostMapping("/getAllList")
	public BaseResponse<List<Menu>> getAllList() {
		return menuService.getAllList();
	}

	@PrimaryDataSource
	@DataScope(role = "1")
	@PostMapping("/save")
	public BaseResponse save(@Valid @RequestBody MenuParam param) {
		return menuService.save(param);
	}

	@PrimaryDataSource
	@DataScope(role = "1")
	@PostMapping("/update")
	public BaseResponse update(@Valid @RequestBody MenuParam param) {
		return menuService.update(param);
	}

	@PrimaryDataSource
	@PostMapping("/del")
	@DataScope(role = "1")
	public BaseResponse del(@RequestBody UpdateStateParam updateStateParam) {
		if (StrUtil.isBlank(updateStateParam.getIds())){
			return BaseResponse.fail("参数错误,id不可为空！");
		}
		return menuService.del(updateStateParam.getIds());
	}
}
