package com.xinkao.erp.user.param;

import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.user.entity.Menu;
import com.xinkao.erp.user.entity.Role;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;


@Data
public class MenuParam implements InputConverter<Menu> {

	private String menuId;
	
	@NotEmpty(message = "菜单名称不能为空")
	private String menuName;

	
	@NotEmpty(message = "父级菜单不能为空")
	private String pid;

	
	@NotEmpty(message = "排序不能为空")
	private String sort;
}
