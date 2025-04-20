package com.xinkao.erp.user.param;

import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.user.entity.Menu;
import com.xinkao.erp.user.entity.Role;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 菜单新增或更新实体
 * @author hys_thanks
 */
@Data
public class MenuParam implements InputConverter<Menu> {

	//修改时有值
	private String menuId;
	/**
	 * 菜单名称
	 */
	@NotEmpty(message = "菜单名称不能为空")
	private String menuName;

	/**
	 * 父级菜单
	 */
	@NotEmpty(message = "父级菜单不能为空")
	private String pid;

	/**
	 * 排序从小到大
	 */
	@NotEmpty(message = "排序不能为空")
	private String sort;
}
