package com.xinkao.erp.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.user.entity.User;
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

	BaseResponse updateState(UpdateStateParam updateStateParam);
}
