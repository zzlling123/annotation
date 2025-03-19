package com.xinkao.erp.system.service;

import com.xinkao.erp.login.entity.UserOptLog;
import com.xinkao.erp.login.vo.UserLoginResultVo;

/**
 * 异步执行日志的存取
 */
public interface AsyncService {
	/**
	 * 账户登录成功或失败日志
	 * @param request
	 * @param resultVo
	 */
    void recordLogininfo(UserLoginResultVo resultVo);
    /**
     * 异步保存用户操作记录
     * @param optLog
     */
    void recordOptLog(UserOptLog optLog);
}
