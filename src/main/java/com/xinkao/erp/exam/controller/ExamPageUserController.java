package com.xinkao.erp.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exam.model.param.ExamPageUserAnswerParam;
import com.xinkao.erp.exam.model.param.ExamPageUserChildAnswerParam;
import com.xinkao.erp.exam.model.param.ExamUserQuery;
import com.xinkao.erp.exam.model.param.SubmitParam;
import com.xinkao.erp.exam.model.vo.ExamPageUserQuestionVo;
import com.xinkao.erp.exam.model.vo.ExamProgressVo;
import com.xinkao.erp.exam.model.vo.ExamUserVo;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.service.ExamPageUserService;
import com.xinkao.erp.exam.vo.ExamPageVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam-page-user")
public class ExamPageUserController {

    @Resource
    private ExamPageUserService examPageUserService;

    @PrimaryDataSource
    @PostMapping("/page")
    public BaseResponse<Page<ExamUserVo>> page(@RequestBody ExamQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<ExamUserVo> voPage = examPageUserService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    @PrimaryDataSource
    @PostMapping("/getExamUserInfo")
    public BaseResponse<ExamUserVo> getExamUserInfo(@RequestBody @Valid ExamUserQuery examUserQuery) {
        return examPageUserService.getExamUserInfo(examUserQuery);
    }

    @PrimaryDataSource
    @PostMapping("/getUserQuestionInfo/{id}")
    public BaseResponse<ExamPageUserQuestionVo> getUserQuestionInfo(@PathVariable("id") String id) {
        return examPageUserService.getUserQuestionInfo(id);
    }

    @PrimaryDataSource
    @PostMapping("/getExamUserProgress")
    public BaseResponse<List<ExamProgressVo>> getExamUserProgress(@RequestBody @Valid ExamUserQuery examUserQuery) {
        return examPageUserService.getExamUserProgress(examUserQuery);
    }

    @PrimaryDataSource
    @RequestMapping("/submitAnswer")
    @Log(content = "提交答案",operationType = OperationType.INSERT)
    public BaseResponse<?> submitAnswer(@Valid @RequestBody ExamPageUserAnswerParam examPageUserAnswerParam) {
        return examPageUserService.submitAnswer(examPageUserAnswerParam);
    }

    @PrimaryDataSource
    @RequestMapping("/submitChildAnswer")
    @Log(content = "子题提交答案",operationType = OperationType.INSERT)
    public BaseResponse<?> submitChildAnswer(@Valid @RequestBody ExamPageUserChildAnswerParam param) {
        return examPageUserService.submitChildAnswer(param);
    }

    @PrimaryDataSource
    @RequestMapping("/submitExam")
    @Log(content = "交卷计算得分",operationType = OperationType.INSERT)
    public BaseResponse<Map<String,Integer>> submitExam(@Valid @RequestBody SubmitParam submitParam) {
        return examPageUserService.submitExam(submitParam);
    }

    @PrimaryDataSource
    @RequestMapping("/heartBeat")
    public BaseResponse<?> heartBeat(@Valid @RequestBody ExamUserQuery examUserQuery) {
        return examPageUserService.heartBeat(examUserQuery);
    }
}
