package com.xinkao.erp.user.mapper;

import com.xinkao.erp.user.entity.Menu;
import com.xinkao.erp.common.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 管理端-菜单表 Mapper 接口
 * </p>
 *
 * @author hanhys
 * @since 2023-08-03 14:13:11
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    List<Menu> getMenuRoleList(@Param("roleId") int roleId);
}
