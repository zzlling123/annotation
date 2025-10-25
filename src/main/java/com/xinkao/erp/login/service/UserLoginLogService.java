package com.xinkao.erp.login.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.login.entity.UserLoginLog;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.system.model.query.UserLoginLogQuery;
import com.xinkao.erp.system.model.vo.UserLoginLogPageVo;

public interface UserLoginLogService extends BaseService<UserLoginLog> {
    Page<UserLoginLogPageVo> page(UserLoginLogQuery query, Pageable pageable);
    boolean saveBy(UserLoginLog userLoginLog);
}
