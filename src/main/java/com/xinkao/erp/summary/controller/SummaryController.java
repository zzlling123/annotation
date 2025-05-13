package com.xinkao.erp.summary.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.exam.entity.ExamPageUser;
import com.xinkao.erp.exam.model.vo.ExamPageUserVo;
import com.xinkao.erp.exam.service.ExamPageUserService;
import com.xinkao.erp.exercise.entity.ExerciseRecords;
import com.xinkao.erp.exercise.service.ExerciseRecordsService;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.manage.service.ClassInfoService;
import com.xinkao.erp.summary.param.ClassSummaryParam;
import com.xinkao.erp.summary.param.SummaryParam;
import com.xinkao.erp.summary.param.SummaryStuParam;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/summary")
public class SummaryController {
    @Autowired
    private ExerciseRecordsService exerciseRecordsService;
    @Autowired
    private ExamPageUserService examPageUserService;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private ClassInfoService classInfoService;

//    统计
//    学生成绩统计	详细记录每个学生的练习和考试成绩，生成个人成绩单和进步曲线图，帮助学生了解自己的学习效果。
//    班级成绩统计	汇总整个班级的成绩数据，生成班级平均分、最高分、最低分等统计信息，便于教师评估教学效果。

    /**
     * 学生成绩统
     * @param summaryStuParam 其中type 是考试还是练习 0 练习 1 考试；stuId 是学生id
     * @return
     */
    @RequestMapping("/stuSummary")
    @ApiOperation("学生成绩统计，type 是考试还是练习 0 练习 1 考试，stuId是学生id")
    @PrimaryDataSource
    public BaseResponse<?> stuSummary(@RequestBody SummaryStuParam  summaryStuParam) {
        //获取当前登录用户信息
        //LoginUser loginUserAll = redisUtil.getInfoByToken();
        Integer useId = summaryStuParam.getStuId();

        if (summaryStuParam.getType() == 0){
            LambdaQueryWrapper<ExerciseRecords> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(ExerciseRecords::getUserId,useId);
            wrapper.orderByAsc(ExerciseRecords::getCreateTime);
            List<ExerciseRecords> exerciseRecordsList = exerciseRecordsService.list(wrapper);
            return BaseResponse.ok(exerciseRecordsList);
        }else if (summaryStuParam.getType() == 1){
            LambdaQueryWrapper<ExamPageUser> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(ExamPageUser::getUserId,useId);
            wrapper.orderByAsc(ExamPageUser::getCreateTime);
            List<ExamPageUser> examPageUserList = examPageUserService.list(wrapper);
            return BaseResponse.ok(examPageUserList);
        }else {
            return BaseResponse.fail("参数错误");
        }
    }

    @RequestMapping("/stuSummaryByUserRole/{type}")
    @ApiOperation("根据登录用户查询所带班级学生成绩统计，admin查看所有学生，type 是考试还是练习 0 练习 1 考试")
    @PrimaryDataSource
    public BaseResponse<?> stuSummaryByUserRole(@PathVariable int type) {
        //获取当前登录用户信息
        List<Integer> userIds = null;
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        if (loginUserAll.getUser().getRoleId() == 1){
            //获取用户管理的班级
            LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(User::getRoleId,2);
            List<User> userList = userService.list(wrapper);
            userIds = userList.stream().map(User::getId).collect(Collectors.toList());
        }else if (loginUserAll.getUser().getRoleId() == 2){
            LambdaQueryWrapper<ClassInfo> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(ClassInfo::getDirectorId,loginUserAll.getUser().getId());
            List<Integer> classIds = classInfoService.list(wrapper).stream().map(ClassInfo::getId).collect(Collectors.toList());
            //获取用户管理的班级
            LambdaQueryWrapper<User> wrapper1 = Wrappers.lambdaQuery();
            wrapper1.in(User::getClassId,classIds);
            wrapper1.eq(User::getRoleId,3);
            List<User> userList = userService.list(wrapper1);
            userIds = userList.stream().map(User::getId).collect(Collectors.toList());
        }else {
            return BaseResponse.fail("参数错误");
        }
        if (userIds != null&&type == 0){
            LambdaQueryWrapper<ExerciseRecords> wrapper = Wrappers.lambdaQuery();
            wrapper.in(ExerciseRecords::getUserId,userIds);
            wrapper.orderByAsc(ExerciseRecords::getCreateTime);
            List<ExerciseRecords> exerciseRecordsList = exerciseRecordsService.list(wrapper);
            return BaseResponse.ok(exerciseRecordsList);
        }else if (userIds != null&&type == 1){
            LambdaQueryWrapper<ExamPageUser> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(ExamPageUser::getUserId,userIds);
            wrapper.orderByAsc(ExamPageUser::getCreateTime);
            List<ExamPageUser> examPageUserList = examPageUserService.list(wrapper);
            return BaseResponse.ok(examPageUserList);
        }else {
            return BaseResponse.fail("参数错误");
        }
    }

    /**
     * 班级成绩统计(汇总整个班级的成绩数据，生成班级平均分、最高分、最低分等统计信息，便于教师评估教学效果。)
     * @param summaryParam 其中type 是考试还是练习 0 练习 1 考试；classId 是班级id
     * @return
     */
    @RequestMapping("/classSummary")
    @ApiOperation("班级成绩统计，summaryParam 其中type 是考试还是练习 0 练习 1 考试；classId 是班级id")
    public BaseResponse<?> classSummary(@RequestBody SummaryParam  summaryParam) {
        //根据班级id获取班级下的所有学生
        List<User> userList = null;
        if (summaryParam.getClassId() == null){
            return BaseResponse.fail("参数错误");
        }else {
            LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(User::getClassId,summaryParam.getClassId());
            userList = userService.list(wrapper);
        }
        if (userList.isEmpty()){
            return BaseResponse.fail("该班级下没有学生");
        }
        List<Integer> userIds = userList.stream().map(User::getId).collect(Collectors.toList());
        if (summaryParam.getType() == 0){
            LambdaQueryWrapper<ExerciseRecords> wrapper = Wrappers.lambdaQuery();
            wrapper.in(ExerciseRecords::getUserId,userIds);
            List<ExerciseRecords> exerciseRecordsList = exerciseRecordsService.list(wrapper);
            return BaseResponse.ok(exerciseRecordsList);
        }
        else if (summaryParam.getType() == 1){
            //关联user表查询考试记录并返回学生姓名
            List<ExamPageUserVo> examPageUserList = examPageUserService.getExamPageUserName(summaryParam.getClassId());
            //获取考试id
            List<Integer> examIds = examPageUserList.stream().map(ExamPageUserVo::getExamId).distinct().collect(Collectors.toList());
            ClassSummaryParam classSummaryParam = new ClassSummaryParam();
            for (Integer examId : examIds){
                List<ExamPageUserVo> examPageUserList1 = examPageUserList.stream().filter(examPageUser -> examPageUser.getExamId().equals(examId)).collect(Collectors.toList());
                Integer maxScore = examPageUserList1.stream().mapToInt(ExamPageUserVo::getScore).max().orElse(0);
                Integer minScore = examPageUserList1.stream().mapToInt(ExamPageUserVo::getScore).min().orElse(0);
                Integer avgScore = examPageUserList1.stream().mapToInt(ExamPageUserVo::getScore).sum()/examPageUserList1.size();
                classSummaryParam.setExamId(examId+"");
                classSummaryParam.setAvgScore(avgScore+"");
                classSummaryParam.setMaxScore(maxScore+"");
                classSummaryParam.setMinScore(minScore+"");
                classSummaryParam.setExamPageUserVoList(examPageUserList1);
            }
            return BaseResponse.ok(classSummaryParam);
        }else {
            return BaseResponse.fail("参数错误");
        }
    }
}
