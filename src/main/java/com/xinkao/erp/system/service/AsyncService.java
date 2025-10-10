package com.xinkao.erp.system.service;

import com.xinkao.erp.login.entity.UserOptLog;
import com.xinkao.erp.login.vo.UserLoginResultVo;


public interface AsyncService {
	
    void recordLogininfo(UserLoginResultVo resultVo);
    
    void recordOptLog(UserOptLog optLog);
}
