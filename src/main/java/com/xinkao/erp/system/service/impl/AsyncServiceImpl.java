package com.xinkao.erp.system.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.xinkao.erp.common.constant.XinKaoConstant;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.util.ip.IpRegionUtils;
import com.xinkao.erp.login.entity.UserLoginLog;
import com.xinkao.erp.login.entity.UserOptLog;
import com.xinkao.erp.login.service.UserLoginLogService;
import com.xinkao.erp.login.service.UserOptLogService;
import com.xinkao.erp.login.vo.UserLoginResultVo;
import com.xinkao.erp.system.service.AsyncService;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j(topic = "sys-user")
@Async
@Service
public class AsyncServiceImpl implements AsyncService {

    @Resource
    private UserLoginLogService loginLogService;
    @Resource
    private UserOptLogService optLogService;
    
    @Override
    public void recordLogininfo(UserLoginResultVo resultVo) {
    	String account = resultVo.getUsername();
    	String realName = resultVo.getRealName();
    	String loginFlag = resultVo.getLoginFlag();
    	String msg = resultVo.getMsg();

    	String ip = resultVo.getIp();
    	ip = StringUtils.isNotBlank(ip)?ip:"";
        String address = StringUtils.isNotBlank(ip)?IpRegionUtils.getRegion(ip):"";

        String os = resultVo.getOs();
        os = StringUtils.isNotBlank(os)?os:"";

        String browser = resultVo.getBrowser();
        browser = StringUtils.isNotBlank(browser)?browser:"";
        
        StringBuilder s = new StringBuilder();
        s.append(getBlock(ip));
        s.append(address);
        s.append(getBlock(account));
        s.append(getBlock(loginFlag));
        s.append(getBlock(msg));

        log.debug(s.toString());

        UserLoginLog loginLog = new UserLoginLog();
        loginLog.setAccount(account);
        loginLog.setRealName(realName);
        loginLog.setIpAddr(ip);
        loginLog.setLoginLocation(address);
        loginLog.setBrowser(browser);
        loginLog.setOs(os);
        loginLog.setMsg(msg);
        loginLog.setLoginTime(DateUtil.now());

        if (XinKaoConstant.LOGIN_SUCCESS.equals(loginFlag) || XinKaoConstant.LOGOUT.equals(loginFlag)) {
            loginLog.setStatus(CommonEnum.LOGIN_STATUS.SUCCESS.getCode());
        } else if (XinKaoConstant.LOGIN_FAIL.equals(loginFlag)) {
            loginLog.setStatus(CommonEnum.LOGIN_STATUS.FAIL.getCode());
        }
        boolean insertResult = loginLogService.saveBy(loginLog);
        if (!insertResult) {
            log.error("登录日志记录失败");
        }
    }

    
    @Override
    public void recordOptLog(UserOptLog optLog){
        boolean insertResult = optLogService.saveBy(optLog);
        if (!insertResult) {
            log.error("操作日志记录失败");
        }
    }

    private String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg.toString() + "]";
    }
}
