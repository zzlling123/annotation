package com.xinkao.erp.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.HandleResult;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.exam.entity.ExamPageUser;
import com.xinkao.erp.exam.service.ExamPageUserService;
import com.xinkao.erp.login.service.UserOptLogService;
import com.xinkao.erp.user.excel.UserImportErrorModel;
import com.xinkao.erp.user.param.UserParam;
import com.xinkao.erp.user.param.UserUpdateParam;
import com.xinkao.erp.user.query.UserQuery;
import com.xinkao.erp.user.vo.UserPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.mapper.UserMapper;
import com.xinkao.erp.user.service.UserService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
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
	@Autowired
	private ExamPageUserService examPageUserService;
	@Value("${resetPassword}")
	private String resetPassword;

	//分页
	@Override
	public Page<UserPageVo> page(UserQuery query, Pageable pageable) {
		Page page = pageable.toPage();
		return userMapper.page(page, query);
	}

	//新增
	@Override
	public BaseResponse save(UserParam userParam){
		if (lambdaQuery().eq(User::getUsername,userParam.getUsername()).count()>0) {
			return BaseResponse.fail("账号已存在！");
		}
		User user = new User();
		BeanUtil.copyProperties(userParam, user);
		//生成密码字段,6位随机盐加passWord
		String salt = RandomUtil.randomString(20);
		String PassWord = SecureUtil.md5(salt+userParam.getPassword());
		user.setSalt(salt);
		user.setPassword(PassWord);
		return save(user)?BaseResponse.ok("新增成功！"): BaseResponse.fail("新增失败！");
	}

	//修改
	@Override
	public BaseResponse update(UserUpdateParam userUpdateParam){
		User oldUser = getById(userUpdateParam.getId());
		User user = BeanUtil.copyProperties(userUpdateParam, User.class);
		//如果classId发生了变动，则同时修改examPageUser中的classId
		updateById(user);
		if (oldUser.getClassId()!=user.getClassId()){
			//涉及一个新旧考试数据的问题(后续再讨论，如果发生了变动，但是新班级没有这个考试，就会丢失数据)
//			examPageUserService.lambdaUpdate().eq(ExamPageUser::getUserId,user.getId()).set(ExamPageUser::getClassId,user.getClassId()).update();
		}
		return BaseResponse.ok("修改成功！");
	}

	//删除
	@Override
	public BaseResponse del(String ids){
		String[] idsArray = StrUtil.splitToArray(ids, ',');
		lambdaUpdate().in(User::getId, idsArray).set(User::getIsDel, CommonEnum.IS_DEL.YES.getCode()).update();
		return BaseResponse.ok("删除成功！");
	}

	//重置密码
	@Override
	public BaseResponse resetPassword(int userId) {
		User user = getById(userId);
		String passwordStr = resetPassword;
		String salt =  RandomUtil.randomString(6);
		String password = SecureUtil.md5(salt+passwordStr);
		user.setPassword(password);
		user.setSalt(salt);
		return updateById(user) ? BaseResponse.ok("重置成功,新密码为："+resetPassword) : BaseResponse.fail("重置失败！");
	}


	@Override
	public BaseResponse updateState(UpdateStateParam updateStateParam){
		String[] ids = updateStateParam.getIds().split(",");
		String content = Objects.equals(updateStateParam.getState(), "1") ? "启用" :"禁用";
		//根据IDS获取列表摘取姓名组成字符串
		String userNames = lambdaQuery().in(User::getId, ids).select(User::getRealName).list().stream().map(User::getRealName).reduce((a, b) -> a + "," + b).get();
		userOptLogService.saveLog("用户"+content+",姓名："+userNames, JSON.toJSONString(updateStateParam));
		return lambdaUpdate().in(User::getId, ids).set(User::getState, updateStateParam.getState()).update()?BaseResponse.ok(content+"成功！"):BaseResponse.fail(content+"失败！");
	}

	@Override
	public void importUser(HttpServletResponse response, Map<Integer, User> addUserMap, HandleResult handleResult, List<UserImportErrorModel> userImportErrorModelList, String token) {

	}
}
