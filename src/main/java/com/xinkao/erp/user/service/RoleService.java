package com.xinkao.erp.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.user.entity.Role;
import com.xinkao.erp.user.param.RoleParam;
import com.xinkao.erp.user.query.RolePageQuery;
import com.xinkao.erp.user.query.RoleQuery;
import com.xinkao.erp.user.vo.RolePageVo;

/**
 * <p>
 * 管理端-角色表 服务类
 * </p>
 *
 * @author hanhys
 * @since 2023-03-29 13:19:13
 */
public interface RoleService extends BaseService<Role> {
    /**
     * 分页
     * @return
     */
    Page<RolePageVo> page(RoleQuery query, Pageable pageable);

    //根据id获取角色权限列表
    BaseResponse getRoleMenuList(RoleParam roleSaveParam);

    //新增角色
    BaseResponse save(RoleParam roleSaveParam);

    //修改角色
    BaseResponse update(RoleParam roleSaveParam);

    //删除角色
    BaseResponse del(Integer id);
}
