package com.xinkao.erp.login.service.impl;

import javax.annotation.Resource;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.system.model.query.UserLoginLogQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.xinkao.erp.common.enums.system.TableSplitEnum;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.core.mybatisplus.handler.DynamicTableHolder;
import com.xinkao.erp.system.model.vo.UserLoginLogPageVo;
import com.xinkao.erp.login.entity.UserLoginLog;
import com.xinkao.erp.login.mapper.UserLoginLogMapper;
import com.xinkao.erp.login.service.UserLoginLogService;
import com.xinkao.erp.system.service.TableOptService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 管理端-用户登录表 服务实现类
 * </p>
 *
 * @author hanhys
 * @since 2023-08-03 14:18:36
 */
@Service
@Slf4j
public class UserLoginLogServiceImpl extends BaseServiceImpl<UserLoginLogMapper, UserLoginLog>
		implements UserLoginLogService {
	@Resource
	private TableOptService tableOptService;

	@Resource
	private UserLoginLogMapper loginLogMapper;

	DefaultIdentifierGenerator idGenerator;

	public UserLoginLogServiceImpl() {
		idGenerator = new DefaultIdentifierGenerator();
	}

	//分页
	@Override
	public Page<UserLoginLogPageVo> page(UserLoginLogQuery query, Pageable pageable) {
		Page page = pageable.toPage();
		if (StrUtil.isNotBlank(query.getEndTime())){
			query.setEndTime(query.getEndTime() + " 23:59:59");
		}
		return loginLogMapper.page(page, query);
	}

	/**
	 * 保存登录日志
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveBy(UserLoginLog userLoginLog) {
		int result = 0;
		try {
			result = loginLogMapper.insert(userLoginLog);
		} catch (Exception e) {
			log.error("保存登录日志失败", e);
		}
		return result == 1;
	}
}
