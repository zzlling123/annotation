package com.xinkao.erp.user.mapper;

import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.query.UserPageQuery;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.user.query.UserQuery;
import com.xinkao.erp.user.vo.UserDepartmentVo;
import com.xinkao.erp.user.vo.UserInfoVo;
import com.xinkao.erp.user.vo.UserPageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 管理端-用户表 Mapper 接口
 * </p>
 *
 * @author hanhys
 * @since 2023-08-03 14:13:11
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
	Page<UserPageVo> page(Page pg , @Param("userQuery") UserQuery userQuery);

	List<String> getDutiesList(@Param("userQuery") UserQuery userQuery);

	List<UserDepartmentVo> getUserDepartmentList();

	UserInfoVo getUserInfoBySelf(@Param("userId") Integer userId);
}
