package com.xinkao.erp.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.HandleResult;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.util.PasswordCheckUtil;
import com.xinkao.erp.common.util.ResultUtils;
import com.xinkao.erp.exam.service.ExamPageUserService;
import com.xinkao.erp.login.service.UserOptLogService;
import com.xinkao.erp.user.excel.UserImportErrorModel;
import com.xinkao.erp.user.param.AccountUpdatePwdParam;
import com.xinkao.erp.user.param.UserParam;
import com.xinkao.erp.user.param.UserUpdateParam;
import com.xinkao.erp.user.query.ExamAndPracticeBarQuery;
import com.xinkao.erp.user.query.UserQuery;
import com.xinkao.erp.user.vo.*;
import lombok.extern.slf4j.Slf4j;
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
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author hanhys
 * @since 2023-03-15 10:19:43
 */
@Slf4j
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
	@Autowired
	private ResultUtils resultUtils;

	//分页
	@Override
	public Page<UserPageVo> page(UserQuery query, Pageable pageable) {
		// 获取当前登录用户角色，用于权限控制
		LoginUser loginUser = redisUtil.getInfoByToken();
		query.setCurrentUserRoleId(loginUser.getUser().getRoleId());

		Page page = pageable.toPage();
		return userMapper.page(page, query);
	}

	//修改新增方法
	@Override
	public BaseResponse save(UserParam userParam){
		// 生成自定义账号ID
		String customId = generateCustomAccountId(userParam);

		// 检查账号是否已存在
		if (lambdaQuery().eq(User::getUsername, customId).count() > 0) {
			return BaseResponse.fail("生成的账号已存在，请重试！");
		}

		User user = new User();
		BeanUtil.copyProperties(userParam, user);
		user.setUsername(customId); // 账号与ID一致

		// 生成密码
		String salt = RandomUtil.randomString(20);
		String password = SecureUtil.md5(salt + userParam.getPassword());
		user.setSalt(salt);
		user.setPassword(password);

		return save(user) ? BaseResponse.ok("新增成功！账号为：" + customId) : BaseResponse.fail("新增失败！");
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
		Integer successCount = handleResult.getSuccessCount();
		List<String> errorList = handleResult.getErrorList();

		if(errorList.isEmpty()){
			if (!addUserMap.isEmpty()) {
				for (Integer rowNum : addUserMap.keySet()) {
					try {
						User addUser = addUserMap.get(rowNum);
						//赋值默认密码
						String passwordStr = resetPassword;
						String salt =  RandomUtil.randomString(6);
						String password = SecureUtil.md5(salt+passwordStr);
						addUser.setPassword(password);
						addUser.setSalt(salt);
						save(addUser);
						successCount++;
					} catch (Exception e) {
						log.error("出现异常: {}", e);
						errorList.add(resultUtils.getErrMsg(rowNum + 1,
								"修改时出现异常：" + e.getMessage()));
					}
				}
			}
			resultUtils.getResult(handleResult,successCount,errorList);
			//记录日志
			if(successCount > 0){
				userOptLogService.saveLog( "导入用户:" + successCount+"条", null);
			}
		}

		if(!errorList.isEmpty()) {
			redisUtil.set(token, JSONObject.toJSONString(BaseResponse.fail("导入失败",userImportErrorModelList)), 2, TimeUnit.HOURS);
		}else{
			redisUtil.set(token, JSONObject.toJSONString(BaseResponse.ok("成功导入数据"+successCount+"条")), 2, TimeUnit.HOURS);
		}
	}

	@Override
	public UserInfoVo getUserInfoBySelf() {
		LoginUser loginUser = redisUtil.getInfoByToken();
		return userMapper.getUserInfoBySelf(loginUser.getUser().getId());
	}

	@Override
	public BaseResponse<?> updatePassword(AccountUpdatePwdParam param){
		LoginUser loginUser = redisUtil.getInfoByToken();
		User user = getById(loginUser.getUser().getId());
		//验证旧密码
		if (!SecureUtil.md5(user.getSalt()+param.getOldPwd()).equals(user.getPassword())) {
			return BaseResponse.fail("密码错误！");
		}
		//验证两次新密码
		if (!param.getNewPwd().equals(param.getNewPwdAgain())) {
			return BaseResponse.fail("两次密码不一致！");
		}
		//验证密码安全性
//		if (!PasswordCheckUtil.evalPassword(param.getNewPwd())) {
//			return BaseResponse.fail("新密码须6-18位，包含字母和数字，不能连续，且必须含有大写和小写字母");
//		}
		String salt =  RandomUtil.randomString(6);
		user.setSalt(salt);
		user.setPassword(SecureUtil.md5(salt+param.getNewPwd()));
		return updateById(user)? BaseResponse.ok("修改成功！"): BaseResponse.fail("修改失败！");
	}

	@Override
	public BaseResponse<List<ExamAndPracticeBarVo>> getExamAndPracticeBar(ExamAndPracticeBarQuery query){
		LoginUser loginUser = redisUtil.getInfoByToken();
		if (query.getQueryType().equals("1")){
			List<ExamAndPracticeBarVo> examAndPracticeBarVoList = new ArrayList<>();
			return BaseResponse.ok(examAndPracticeBarVoList);
		}else {
			//考试
			List<ExamAndPracticeBarVo> examAndPracticeBarVoList = userMapper.getExamAndPracticeBarForExam(query,loginUser.getUser().getId());
			for (ExamAndPracticeBarVo examAndPracticeBarVo : examAndPracticeBarVoList) {
				//赋值百分比
				if ("0".equals(examAndPracticeBarVo.getUserScore()) || "0".equals(examAndPracticeBarVo.getScore())){
					examAndPracticeBarVo.setScoreRate("0.00");
				}else{
					examAndPracticeBarVo.setScoreRate(NumberUtil.round(new BigDecimal(examAndPracticeBarVo.getUserScore()).divide(new BigDecimal(examAndPracticeBarVo.getScore()),2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)),2).toString());
				}
			}
			return BaseResponse.ok(examAndPracticeBarVoList);
		}
	}

	@Override
	public BaseResponse<List<ExamAndPracticePieAllVo>> getExamAndPracticePie(ExamAndPracticeBarQuery query){
		LoginUser loginUser = redisUtil.getInfoByToken();
		if (query.getQueryType().equals("1")){
			List<ExamAndPracticePieAllVo> voList = new ArrayList<>();
			return BaseResponse.ok(voList);
		}else {
			//考试
			List<ExamAndPracticePieVo> examAndPracticePieVoList = userMapper.getExamAndPracticePieForExam(query,loginUser.getUser().getId());
			List<ExamAndPracticePieAllVo> allVoList = new ArrayList<>();
			int allNum = 0;
			for (ExamAndPracticePieVo examAndPracticePieVo : examAndPracticePieVoList) {
				ExamAndPracticePieAllVo vo = new ExamAndPracticePieAllVo();
				allNum += Integer.parseInt(examAndPracticePieVo.getTeaNum());
				allNum = allNum - Integer.parseInt(examAndPracticePieVo.getUserTeaNum());
				vo.setTypeName(examAndPracticePieVo.getTypeName());
				vo.setUserTeaNum(examAndPracticePieVo.getUserTeaNum());
				allVoList.add(vo);
			}
			ExamAndPracticePieAllVo vo = new ExamAndPracticePieAllVo();
			vo.setTypeName("未完成");
			vo.setUserTeaNum(Integer.toString(allNum));
			allVoList.add(vo);
			return BaseResponse.ok(allVoList);
		}
	}


	@Override
	public String generateCustomAccountId(UserParam userParam) {
		// 返回的账号格式：学校管理员用XX,社保局管理员用BJ，教师JS，学生XS，评审专家ZJ，社会考生SH，后面加手机
		// 学校管理员的角色id为18,社保局管理员Id为19，教师角色Id为2，学生角色id为3，评审专家角色id为20，社会考生的角色id为21
		String customId = null;

		String roleId = userParam.getRoleId();
		String mobile = userParam.getMobile();

		// 根据角色ID生成前缀
		String prefix = "";
		switch (roleId) {
			case "18":
				prefix = "XX"; // 学校管理员
				break;
			case "19":
				prefix = "BJ"; // 社保局管理员
				break;
			case "2":
				prefix = "JS"; // 教师
				break;
			case "3":
				prefix = "XS"; // 学生
				break;
			case "20":
				prefix = "ZJ"; // 评审专家
				break;
			case "21":
				prefix = "SH"; // 社会考生
				break;
			default:
				prefix = "YH"; // 默认用户
				break;
		}

		// 组合前缀和手机号生成账号
		customId = prefix + mobile;

		return customId;
	}
}
