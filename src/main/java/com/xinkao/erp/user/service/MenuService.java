package com.xinkao.erp.user.service;

import java.util.List;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.user.entity.Menu;
import com.xinkao.erp.user.param.MenuParam;

/**
 * <p>
 * 管理端-菜单表 服务类
 * </p>
 *
 * @author hanhys
 * @since 2023-04-14 17:17:06
 */
public interface MenuService extends BaseService<Menu> {

    //根据用户权限获取用户菜单
    BaseResponse<List<Menu>> getList();

    List<Menu> formatMenuList(List<Menu> menuList);

    //新增
    BaseResponse save(MenuParam departmentParam);

    //修改
    BaseResponse update(MenuParam departmentParam);

    //删除
    BaseResponse del(String id);
}
