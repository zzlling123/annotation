package com.xinkao.erp.user.service;

import java.util.List;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.user.entity.Menu;
import com.xinkao.erp.user.param.MenuParam;

public interface MenuService extends BaseService<Menu> {

    BaseResponse<List<Menu>> getList();

    BaseResponse<List<Menu>> getAllList();

    List<Menu> formatMenuList(List<Menu> menuList);

    BaseResponse save(MenuParam departmentParam);

    BaseResponse update(MenuParam departmentParam);

    BaseResponse del(String id);
}
