package com.xinkao.erp.login.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.login.entity.UserOptLog;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.system.model.query.UserOptLogQuery;
import com.xinkao.erp.system.model.vo.UserOptLogDetailsVo;
import com.xinkao.erp.system.model.vo.UserOptLogPageVo;

/**
 * <p>
 * 管理端-操作记录(分表) 服务类
 * </p>
 *
 * @author hanhys
 * @since 2023-08-03 14:18:36
 */
public interface UserOptLogService extends BaseService<UserOptLog> {
	/**
	 * 保存操作记录
	 * @param optLog
	 * @return
	 */
	boolean saveBy(UserOptLog optLog);

	Page<UserOptLogPageVo> page(UserOptLogQuery query, Pageable pageable);

	UserOptLogDetailsVo details(Integer id);

	void saveLog(String content,String json);

}
