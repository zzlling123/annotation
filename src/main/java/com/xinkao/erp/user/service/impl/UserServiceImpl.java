package com.xinkao.erp.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.login.service.UserOptLogService;
import com.xinkao.erp.user.query.UserQuery;
import com.xinkao.erp.user.vo.UserPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.mapper.UserMapper;
import com.xinkao.erp.user.service.UserService;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author hanhys
 * @since 2023-03-15 10:19:43
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements UserService {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserOptLogService userOptLogService;


	@Override
	public BaseResponse updateState(UpdateStateParam updateStateParam){
		String[] ids = updateStateParam.getIds().split(",");
		String content = Objects.equals(updateStateParam.getState(), "1") ? "启用" :"禁用";
		//根据IDS获取列表摘取姓名组成字符串
		String userNames = lambdaQuery().in(User::getId, ids).select(User::getRealName).list().stream().map(User::getRealName).reduce((a, b) -> a + "," + b).get();
		userOptLogService.saveLog("用户"+content+",姓名："+userNames, JSON.toJSONString(updateStateParam));
		return lambdaUpdate().in(User::getId, ids).set(User::getState, updateStateParam.getState()).update()?BaseResponse.ok(content+"成功！"):BaseResponse.fail(content+"失败！");
	}
}
