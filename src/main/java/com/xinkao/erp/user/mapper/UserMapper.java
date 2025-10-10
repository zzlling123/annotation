package com.xinkao.erp.user.mapper;

import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.query.ExamAndPracticeBarQuery;
import com.xinkao.erp.user.query.UserPageQuery;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.user.query.UserQuery;
import com.xinkao.erp.user.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface UserMapper extends BaseMapper<User> {
	Page<UserPageVo> page(Page pg , @Param("userQuery") UserQuery userQuery);

	List<String> getDutiesList(@Param("userQuery") UserQuery userQuery);

	List<UserDepartmentVo> getUserDepartmentList();

	UserInfoVo getUserInfoBySelf(@Param("userId") Integer userId);

	List<ExamAndPracticeBarVo> getExamAndPracticeBarForExam(ExamAndPracticeBarQuery query,Integer userId);

	List<ExamAndPracticePieVo> getExamAndPracticePieForExam(ExamAndPracticeBarQuery query, Integer userId);

	List<ExamAndPracticeBarVo> getPracticeBarForExercise(ExamAndPracticeBarQuery query,Integer userId);

	List<ExamAndPracticePieVo> getPracticePieForExercise(ExamAndPracticeBarQuery query, Integer userId);
}
