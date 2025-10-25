package com.xinkao.erp.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.user.entity.Menu;
import com.xinkao.erp.user.entity.Role;
import com.xinkao.erp.user.param.RoleParam;
import com.xinkao.erp.user.query.RolePageQuery;
import com.xinkao.erp.user.query.RoleQuery;
import com.xinkao.erp.user.vo.RolePageVo;

import java.util.List;

public interface RoleService extends BaseService<Role> {

    Page<RolePageVo> page(RoleQuery query, Pageable pageable);

    BaseResponse<List<Menu>> getRoleMenuList(RoleParam roleSaveParam);

    BaseResponse save(RoleParam roleSaveParam);

    BaseResponse update(RoleParam roleSaveParam);

    BaseResponse del(Integer id);

    List<Role> getRoleList();
}
