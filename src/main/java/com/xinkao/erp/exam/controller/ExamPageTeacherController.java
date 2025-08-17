package com.xinkao.erp.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.DataScope;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.exam.entity.ExamExpert;
import com.xinkao.erp.exam.entity.ExamExpertAssignment;
import com.xinkao.erp.exam.model.param.ExamPageUserAnswerParam;
import com.xinkao.erp.exam.model.param.ExamUserQuery;
import com.xinkao.erp.exam.model.param.SubmitParam;
import com.xinkao.erp.exam.model.vo.ExamProgressVo;
import com.xinkao.erp.exam.model.vo.ExamUserVo;
import com.xinkao.erp.exam.param.ExamCorrectChildParam;
import com.xinkao.erp.exam.param.ExamCorrectParam;
import com.xinkao.erp.exam.query.ExamExpertsQuery;
import com.xinkao.erp.exam.query.ExamTeacherQuery;
import com.xinkao.erp.exam.service.ExamExpertAssignmentService;
import com.xinkao.erp.exam.service.ExamExpertService;
import com.xinkao.erp.exam.service.ExamPageUserService;
import com.xinkao.erp.exam.vo.ExamPageAnswerVo;
import com.xinkao.erp.exam.vo.ExamPageTeacherVo;
import com.xinkao.erp.exam.vo.ExamPageUserListVo;
import com.xinkao.erp.exam.vo.ExamPageVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教师批改管理
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@RestController
@RequestMapping("/exam-page-teacher")
public class ExamPageTeacherController {

    @Resource
    private ExamPageUserService examPageUserService;

    @Autowired
    private ExamExpertService examExpertService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ExamExpertAssignmentService examExpertAssignmentService;

    @PrimaryDataSource
    @DataScope(role = "2")
    @PostMapping("/page")
    @ApiOperation("根据当前登录教师获取考试列表(已批阅/应批阅)(固定筛选一个班级)")
    public BaseResponse<Page<ExamPageTeacherVo>> page(@RequestBody @Valid ExamTeacherQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<ExamPageTeacherVo> voPage = examPageUserService.pageTeacher(query, pageable);
        return BaseResponse.ok("成功",voPage);
    }

    @PrimaryDataSource
    @DataScope(role = "2")
    @PostMapping("/getExamUserListForExamId")
    @ApiOperation("根据试卷id、班级ID获取学生列表")
    public BaseResponse<Page<ExamPageUserListVo>> getExamUserListForExamId(@RequestBody @Valid ExamUserQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<ExamPageUserListVo> voPage = examPageUserService.getExamUserListForExamId(query, pageable);
        return BaseResponse.ok("成功",voPage);
    }

    @PrimaryDataSource
    @DataScope(role = "2,20")
    @PostMapping("/getExamUserInfo/{examPageUserId}")
    @ApiOperation("点击学生，获取该学生此次试卷答题信息")
    public BaseResponse<ExamPageAnswerVo> getExamUserAnswerInfo(@PathVariable String examPageUserId) {
        return examPageUserService.getExamUserAnswerInfo(examPageUserId);
    }

    @PrimaryDataSource
    @DataScope(role = "2,20")
    @PostMapping("/correct")
    @ApiOperation("批改，提交分数(如果是该学生该试卷最后一道问答题则会进行计算总分)")
    @Log(content = "批改",operationType = OperationType.INSERT)
    public BaseResponse<?> correct(@RequestBody @Valid ExamCorrectParam param) {
        return examPageUserService.correct(param);
    }

    @PrimaryDataSource
    @DataScope(role = "2,20")
    @PostMapping("/correctChild")
    @ApiOperation("子题批改，提交分数(如果是该学生该题目单最后一道题则会进行计算该题总分)")
    @Log(content = "子题批改",operationType = OperationType.INSERT)
    public BaseResponse<?> correctChild(@RequestBody @Valid ExamCorrectChildParam param) {
        return examPageUserService.correctChild(param);
    }


    @PrimaryDataSource
    @PostMapping("/getExamExperts")
    @ApiOperation("获取专家判卷考试列表信息-分页")
    public BaseResponse<Page<ExamPageVo>> getExamProgress(@RequestBody @Valid BasePageQuery query) {
        Pageable pageable = query.getPageInfo();
        //获取当前登录用户id
        LoginUser loginUser = redisUtil.getInfoByToken();
        Integer expertId = loginUser.getUser().getId();
        List<ExamExpert> examExperts = examExpertService.getExamsByExpertId(expertId);
        //获取该专家的考试列表
        List<Integer> examIds = examExperts.stream().map(ExamExpert::getExamId).collect(Collectors.toList());
        Page<ExamPageVo> voPage = examExpertService.getExamByExamId(examIds, pageable);
        return BaseResponse.ok("成功",voPage);
    }

    @PrimaryDataSource
    @PostMapping("/getUserByExamExperts")
    @ApiOperation("通过考试id获取专家考试学生列表-分页")
    public BaseResponse<Page<ExamPageUserListVo>> getUserByExamExperts(@RequestBody @Valid ExamExpertsQuery query) {
        Pageable pageable = query.getPageInfo();
        //获取当前登录用户id
        LoginUser loginUser = redisUtil.getInfoByToken();
        Integer expertId = loginUser.getUser().getId();
        List<ExamExpertAssignment> list = examExpertAssignmentService.getAssignmentsByExamIdAndExpertId(query.getExamId(), expertId);
        //获取examExperts中的userId的集合
        List<Integer> userIds = list.stream().map(ExamExpertAssignment::getUserId).collect(Collectors.toList());
        //通过examId和userId获取ExamPageUser信息
        Page<ExamPageUserListVo> voPage = examPageUserService.getExamUserListForExamIdByUserIds(query.getExamId(), userIds, pageable);
        return BaseResponse.ok("成功",voPage);
    }


}
