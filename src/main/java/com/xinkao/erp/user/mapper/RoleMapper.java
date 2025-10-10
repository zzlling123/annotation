package com.xinkao.erp.user.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.user.entity.Role;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.user.query.RoleQuery;
import com.xinkao.erp.user.vo.RolePageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    Page<RolePageVo> page(Page pg , @Param("roleQuery") RoleQuery query);
}
