package com.xinkao.erp.user.controller;

import java.util.List;

import javax.annotation.Resource;

import com.xinkao.erp.user.service.MenuService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
	@PostMapping("/getList")
	@ApiOperation("根据用户权限获取用户菜单")
	public BaseResponse getList() {
		return menuService.getList();
	}
}
