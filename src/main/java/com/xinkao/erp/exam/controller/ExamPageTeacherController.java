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
    public BaseResponse<Page<ExamPageTeacherVo>> page(@RequestBody @Valid ExamTeacherQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<ExamPageTeacherVo> voPage = examPageUserService.pageTeacher(query, pageable);
        return BaseResponse.ok("成功",voPage);
    }

    @PrimaryDataSource
    @DataScope(role = "2")
    @PostMapping("/getExamUserListForExamId")
    public BaseResponse<Page<ExamPageUserListVo>> getExamUserListForExamId(@RequestBody @Valid ExamUserQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<ExamPageUserListVo> voPage = examPageUserService.getExamUserListForExamId(query, pageable);
        return BaseResponse.ok("成功",voPage);
    }

    @PrimaryDataSource
    @DataScope(role = "2,20")
    @PostMapping("/getExamUserInfo/{examPageUserId}")
    public BaseResponse<ExamPageAnswerVo> getExamUserAnswerInfo(@PathVariable String examPageUserId) {
        return examPageUserService.getExamUserAnswerInfo(examPageUserId);
    }

    @PrimaryDataSource
    @DataScope(role = "2,20")
    @PostMapping("/correct")
    public BaseResponse<?> correct(@RequestBody @Valid ExamCorrectParam param) {
        return examPageUserService.correct(param);
    }

    @PrimaryDataSource
    @DataScope(role = "2,20")
    @PostMapping("/correctChild")
    public BaseResponse<?> correctChild(@RequestBody @Valid ExamCorrectChildParam param) {
        return examPageUserService.correctChild(param);
    }


    @PrimaryDataSource
    @PostMapping("/getExamExperts")
    public BaseResponse<Page<ExamPageVo>> getExamProgress(@RequestBody @Valid BasePageQuery query) {
        Pageable pageable = query.getPageInfo();
        LoginUser loginUser = redisUtil.getInfoByToken();
        Integer expertId = loginUser.getUser().getId();
        List<ExamExpert> examExperts = examExpertService.getExamsByExpertId(expertId);
        List<Integer> examIds = examExperts.stream().map(ExamExpert::getExamId).collect(Collectors.toList());
        Page<ExamPageVo> voPage = examExpertService.getExamByExamId(examIds, pageable);
        return BaseResponse.ok("成功",voPage);
    }

    @PrimaryDataSource
    @PostMapping("/getUserByExamExperts")
    public BaseResponse<Page<ExamPageUserListVo>> getUserByExamExperts(@RequestBody @Valid ExamExpertsQuery query) {
        Pageable pageable = query.getPageInfo();
        LoginUser loginUser = redisUtil.getInfoByToken();
        Integer expertId = loginUser.getUser().getId();
        List<ExamExpertAssignment> list = examExpertAssignmentService.getAssignmentsByExamIdAndExpertId(query.getExamId(), expertId);
        List<Integer> userIds = list.stream().map(ExamExpertAssignment::getUserId).collect(Collectors.toList());
        Page<ExamPageUserListVo> voPage = examPageUserService.getExamUserListForExamIdByUserIds(query.getExamId(), userIds, pageable);
        return BaseResponse.ok("成功",voPage);
    }


}
