package com.xinkao.erp.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.HandleResult;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.excel.UserImportErrorModel;
import com.xinkao.erp.user.param.AccountUpdatePwdParam;
import com.xinkao.erp.user.param.UserParam;
import com.xinkao.erp.user.param.UserUpdateParam;
import com.xinkao.erp.user.query.ExamAndPracticeBarQuery;
import com.xinkao.erp.user.query.UserQuery;
import com.xinkao.erp.user.vo.ExamAndPracticeBarVo;
import com.xinkao.erp.user.vo.ExamAndPracticePieVo;
import com.xinkao.erp.user.vo.UserInfoVo;
import com.xinkao.erp.user.vo.UserPageVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author hanhys
 * @since 2023-03-15 10:19:43
 */
public interface UserService extends BaseService<User> {

	/**
	 * 分页
	 * @return
	 */
	Page<UserPageVo> page(UserQuery query, Pageable pageable);

	//新增用户
	BaseResponse<?> save(UserParam userSaveParam);

	//修改用户
	BaseResponse<?> update(UserUpdateParam userUpdateParam);

	//修改状态
	BaseResponse<?> updateState(UpdateStateParam updateStateParam);

	//重置密码
	BaseResponse<?> resetPassword(int userId);

	//删除用户
	BaseResponse<?> del(String ids);

	void importUser(HttpServletResponse response, Map<Integer, User> addUserMap, HandleResult handleResult, List<UserImportErrorModel> userImportErrorModelList, String token);

	UserInfoVo getUserInfoBySelf();

	BaseResponse<?> updatePassword(AccountUpdatePwdParam param);

	BaseResponse<List<ExamAndPracticeBarVo>> getExamAndPracticeBar(ExamAndPracticeBarQuery query);

	BaseResponse<List<ExamAndPracticePieVo>> getExamAndPracticePie(ExamAndPracticeBarQuery query);
}
