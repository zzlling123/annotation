package com.xinkao.erp.login.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.system.LogStatus;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.util.ServletUtils;
import com.xinkao.erp.common.util.ip.IpRegionUtils;
import com.xinkao.erp.common.util.ip.IpUtils;
import com.xinkao.erp.login.entity.UserOptLog;
import com.xinkao.erp.login.mapper.UserOptLogMapper;
import com.xinkao.erp.login.service.UserOptLogService;
import com.xinkao.erp.system.model.query.UserOptLogQuery;
import com.xinkao.erp.system.model.vo.UserOptLogPageVo;
import com.xinkao.erp.system.service.AsyncService;
import com.xinkao.erp.system.service.TableOptService;

import cn.hutool.core.date.DateUtil;
import com.xinkao.erp.user.entity.User;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.xinkao.erp.common.enums.system.TableSplitEnum;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.core.mybatisplus.handler.DynamicTableHolder;
import com.xinkao.erp.system.model.vo.UserOptLogDetailsVo;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserOptLogServiceImpl extends BaseServiceImpl<UserOptLogMapper, UserOptLog> implements UserOptLogService {

	@Resource
	private TableOptService tableOptService;
	@Resource
	private UserOptLogMapper optLogMapper;

	private DefaultIdentifierGenerator idGenerator;

	public UserOptLogServiceImpl() {
		idGenerator = new DefaultIdentifierGenerator();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveBy(UserOptLog optLog) {
		optLog.setDateStr(DateUtil.date().toDateStr());
		int result = 0;
		try {
			result = optLogMapper.insert(optLog);
		} catch (Exception e) {
			log.error("保存操作日志失败", e);
		}
		return result == 1;
	}

	@Override
	public Page<UserOptLogPageVo> page(UserOptLogQuery query, Pageable pageable) {
		Page page = pageable.toPage();
		if (StrUtil.isNotBlank(query.getEndTime())){
			query.setEndTime(query.getEndTime() + " 23:59:59");
		}
		return optLogMapper.page(page, query);
	}

	@Override
	public UserOptLogDetailsVo details(Integer id) {
		return optLogMapper.details(id);
	}

	@Override
	public void saveLog(String content,String json) {
		LoginUser loginUser = redisUtil.getInfoByToken();
		User user = loginUser.getUser();
		if (StrUtil.isBlank(content)){
			return;
		}
		UserOptLog userOptLog = new UserOptLog();
		userOptLog.setAccount(user.getRealName());
		userOptLog.setUserId(user.getId().toString());
		userOptLog.setRealName(user.getRealName());
		userOptLog.setStatus(LogStatus.SUCCESS.getCode());
		userOptLog.setRequestParam(json);
		userOptLog.setContent(content);
		userOptLog.setRequestTime(DateUtil.now());

		String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
		userOptLog.setClientIp(ip);
		userOptLog.setIpRegion(IpRegionUtils.getRegion(ip));
		String userAgentStr = ServletUtils.getRequest().getHeader("User-Agent");
		UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
		userOptLog.setBrowser(userAgent.getBrowser().getName());
		userOptLog.setOs(userAgent.getOs().getName());
		userOptLog.setUserAgent(userAgentStr);

		userOptLog.setRequestMethod(ServletUtils.getRequest().getMethod());
		userOptLog.setRequestUrl(ServletUtils.getRequest().getRequestURI());
		SpringUtil.getBean(AsyncService.class).recordOptLog(userOptLog);
	}
}
