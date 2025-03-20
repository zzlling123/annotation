package com.xinkao.erp.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.param.UserParam;
import com.xinkao.erp.user.param.UserUpdateParam;
import com.xinkao.erp.user.query.UserQuery;
import com.xinkao.erp.user.vo.UserPageVo;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author hanhys
 * @since 2023-03-15 10:19:43
 */
public interface UserService extends BaseService<User> {

	/**
	 * 分页
	 * @return
	 */
	Page<UserPageVo> page(UserQuery query, Pageable pageable);

	//新增用户
	BaseResponse save(UserParam userSaveParam);

	//修改用户
	BaseResponse update(UserUpdateParam userUpdateParam);


	BaseResponse updateState(UpdateStateParam updateStateParam);

	BaseResponse resetPassword(int userId);

	//删除用户
	BaseResponse del(String ids);
}
