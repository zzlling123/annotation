package com.xinkao.erp.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.DataAuth;
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

/**
 * 角色相关
 * @author 777
 *
 */
@RequestMapping("/role")
@RestController
public class RoleController extends BaseController {
	@Autowired
	private RoleService roleService;
	/**
	 * 根据用户权限获取用户菜单
	 *
	 * @return
	 */
	@PostMapping("/getList")
	@ApiOperation("根据用户权限获取用户菜单")
	public BaseResponse<List<Role>> getList() {
		return BaseResponse.ok(roleService.lambdaQuery()
				.eq(Role::getIsDel, CommonEnum.IS_DEL.NO.getCode()).list());
	}

	/**
	 * 分页
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/page")
	@ApiOperation("分页")
	public BaseResponse<Page<RolePageVo>> page(@RequestBody RoleQuery query) {
		//获取用户信息
		Pageable pageable = query.getPageInfo();
		Page<RolePageVo> voPage = roleService.page(query, pageable);
		return BaseResponse.ok(voPage);
	}

	/**
	 * 根据id获取角色权限列表
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/getRoleMenuList")
	@ApiOperation("根据id获取角色权限列表")
	public BaseResponse<List<Menu>> getRoleMenuList(@RequestBody RoleParam roleParam) {
		return roleService.getRoleMenuList(roleParam);
	}


	/**
	 * 新增角色
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/save")
	@ApiOperation("新增角色")
	public BaseResponse save(@Valid @RequestBody RoleParam roleParam) {
		return roleService.save(roleParam);
	}

	/**
	 * 修改角色
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/update")
	@ApiOperation("修改角色")
	public BaseResponse update(@Valid @RequestBody RoleParam roleParam) {
		return roleService.update(roleParam);
	}

	/**
	 * 删除
	 *
	 * @return
	 */
	@PrimaryDataSource
	@PostMapping("/del")
	@ApiOperation("删除角色")
	public BaseResponse del(@RequestBody Role role) {
		if (role.getId() == null){
			return BaseResponse.fail("参数错误,id不可为空！");
		}
		return roleService.del(role.getId());
	}
}
