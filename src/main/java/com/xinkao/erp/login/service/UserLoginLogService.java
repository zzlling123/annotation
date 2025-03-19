package com.xinkao.erp.login.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.login.entity.UserLoginLog;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.system.model.query.UserLoginLogQuery;
import com.xinkao.erp.system.model.vo.UserLoginLogPageVo;

/**
 * <p>
 * 管理端-用户登录表 服务类
 * </p>
 *
 * @author hanhys
 * @since 2023-08-03 14:18:36
 */
public interface UserLoginLogService extends BaseService<UserLoginLog> {
    /**
     * 分页
     * @return
     */
    Page<UserLoginLogPageVo> page(UserLoginLogQuery query, Pageable pageable);
    /**
     * 分表保存数据
     * @return
     */
    boolean saveBy(UserLoginLog userLoginLog);
}
