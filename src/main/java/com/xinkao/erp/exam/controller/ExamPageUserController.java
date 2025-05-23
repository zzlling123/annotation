package com.xinkao.erp.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exam.model.param.ExamPageUserAnswerParam;
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

/**
 * 考生、答题管理
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@RestController
@RequestMapping("/exam-page-user")
public class ExamPageUserController {

    @Resource
    private ExamPageUserService examPageUserService;

    /**
     *
     * 根据用户信息获取考试列表
     */
    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("根据用户信息获取考试列表")
    public BaseResponse<Page<ExamUserVo>> page(@RequestBody ExamQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<ExamUserVo> voPage = examPageUserService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    /**
     *
     * 根据用户ID，考试ID获取试卷信息
     */
    @PrimaryDataSource
    @PostMapping("/getExamUserInfo")
    @ApiOperation("获取试卷信息")
    public BaseResponse<ExamUserVo> getExamUserInfo(@RequestBody @Valid ExamUserQuery examUserQuery) {
        return examPageUserService.getExamUserInfo(examUserQuery);
    }

    /**
     *
     * 获取题目信息
     */
    @PrimaryDataSource
    @PostMapping("/getUserQuestionInfo/{id}")
    @ApiOperation("获取题目信息")
    public BaseResponse<ExamPageUserQuestionVo> getUserQuestionInfo(@PathVariable("id") String id) {
        return examPageUserService.getUserQuestionInfo(id);
    }



    /**
     *
     * 根据此次考试用户ID，考试ID获取答题进度
     */
    @PrimaryDataSource
    @PostMapping("/getExamUserProgress")
    @ApiOperation("获取答题进度")
    public BaseResponse<List<ExamProgressVo>> getExamUserProgress(@RequestBody @Valid ExamUserQuery examUserQuery) {
        return examPageUserService.getExamUserProgress(examUserQuery);
    }



    /**
     *
     * 提交答案
     */
    @PrimaryDataSource
    @RequestMapping("/submitAnswer")
    @ApiOperation("单题提交答案")
    @Log(content = "提交答案",operationType = OperationType.INSERT)
    public BaseResponse<?> submitAnswer(@Valid @RequestBody ExamPageUserAnswerParam examPageUserAnswerParam) {
        return examPageUserService.submitAnswer(examPageUserAnswerParam);
    }


    /**
     *
     * 交卷计算得分
     */
    @PrimaryDataSource
    @RequestMapping("/submitExam")
    @ApiOperation("交卷计算得分")
    @Log(content = "交卷计算得分",operationType = OperationType.INSERT)
    public BaseResponse<Map<String,Integer>> submitExam(@Valid @RequestBody SubmitParam submitParam) {
        return examPageUserService.submitExam(submitParam);
    }

    /**
     *
     * 心跳检测
     */
    @PrimaryDataSource
    @RequestMapping("/heartBeat")
    @ApiOperation("心跳检测")
    public BaseResponse<?> heartBeat(@Valid @RequestBody ExamUserQuery examUserQuery) {
        return examPageUserService.heartBeat(examUserQuery);
    }
}
