package com.xinkao.erp.user.mapper;

import com.xinkao.erp.user.entity.Menu;
import com.xinkao.erp.common.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    List<Menu> getMenuRoleList(@Param("roleId") int roleId);
}
