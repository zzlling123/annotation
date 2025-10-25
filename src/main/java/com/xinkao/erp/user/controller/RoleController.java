package com.xinkao.erp.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.DataAuth;
import com.xinkao.erp.common.annotation.DataScope;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.enums.busi.UserLevelEnum;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.user.entity.Menu;
import com.xinkao.erp.user.entity.Role;
import com.xinkao.erp.user.param.RoleParam;
import com.xinkao.erp.user.query.RoleQuery;
import com.xinkao.erp.user.service.MenuService;
import com.xinkao.erp.user.service.RoleService;
import com.xinkao.erp.user.vo.RolePageVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/role")
@RestController
public class RoleController extends BaseController {
	@Autowired
	private RoleService roleService;
	@PostMapping("/getList")
	public BaseResponse<List<Role>> getList() {
		return BaseResponse.ok(roleService.getRoleList());
	}

	@PrimaryDataSource
	@PostMapping("/page")
	@DataScope(role = "1")
	public BaseResponse<Page<RolePageVo>> page(@RequestBody RoleQuery query) {
		Pageable pageable = query.getPageInfo();
		Page<RolePageVo> voPage = roleService.page(query, pageable);
		return BaseResponse.ok(voPage);
	}

	@PrimaryDataSource
	@PostMapping("/getRoleMenuList")
	public BaseResponse<List<Menu>> getRoleMenuList(@RequestBody RoleParam roleParam) {
		return roleService.getRoleMenuList(roleParam);
	}

	@PrimaryDataSource
	@DataScope(role = "1")
	@PostMapping("/save")
	public BaseResponse save(@Valid @RequestBody RoleParam roleParam) {
		return roleService.save(roleParam);
	}

	@PrimaryDataSource
	@DataScope(role = "1")
	@PostMapping("/update")
	public BaseResponse update(@Valid @RequestBody RoleParam roleParam) {
		return roleService.update(roleParam);
	}

	@PrimaryDataSource
	@DataScope(role = "1")
	@PostMapping("/del")
	public BaseResponse del(@RequestBody Role role) {
		if (role.getId() == null){
			return BaseResponse.fail("参数错误,id不可为空！");
		}
		return roleService.del(role.getId());
	}
}
