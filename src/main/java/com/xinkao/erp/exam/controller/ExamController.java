package com.xinkao.erp.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.DataScope;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.exam.excel.ExamPageSetVo;
import com.xinkao.erp.exam.param.ExamPageSetParam;
import com.xinkao.erp.exam.param.ExamParam;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.service.ExamPageSetService;
import com.xinkao.erp.exam.service.ExamService;
import com.xinkao.erp.exam.vo.ExamDetailVo;
import com.xinkao.erp.exam.vo.ExamPageVo;
import com.xinkao.erp.question.service.QuestionTypeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/exam")
public class ExamController {

    @Autowired
    private ExamService examService;

    @Resource
    protected RedisUtil redisUtil;

    @Autowired
    private QuestionTypeService questionTypeService;

    @Resource
    private ExamPageSetService examPageSetService;

    @PrimaryDataSource
    @PostMapping("/page")
    public BaseResponse<Page<ExamPageVo>> page(@RequestBody ExamQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<ExamPageVo> voPage = examService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    @PrimaryDataSource
    @DataScope(role = "1,2,18,19")
    @GetMapping("/detail/{id}")
    public BaseResponse<ExamDetailVo> detail(@PathVariable Integer id) {
        ExamDetailVo examDetailVo = examService.detail(id);
        return BaseResponse.ok(examDetailVo);
    }

    @PrimaryDataSource
    @DataScope(role = "1,2,18,19")
    @PostMapping("/save")
    public BaseResponse<?> save(@Valid @RequestBody ExamParam examParam) {
        return examService.save(examParam);
    }

    @PrimaryDataSource
    @DataScope(role = "1,2,18,19")
    @PostMapping("/update")
    public BaseResponse<?> update(@Valid @RequestBody ExamParam examParam) {
        return examService.update(examParam);
    }

    @PrimaryDataSource
    @DataScope(role = "1,2,18,19")
    @PostMapping("/del/{id}")
    public BaseResponse<?> del(@PathVariable Integer id) {
        return examService.del(id);
    }

    @PrimaryDataSource
    @DataScope(role = "1,2,18,19")
    @PostMapping("/getExamTypeSet")
    public BaseResponse<List<ExamPageSetVo>> getExamTypeSet(@RequestParam String examId) {
        return BaseResponse.ok(examService.getExamPageSetByTypeAndShape(examId));
    }

    @PrimaryDataSource
    @PostMapping("/saveExamPageSetPoint/{examId}")
    public BaseResponse<?> saveExamPageSetPoint(@PathVariable String examId,@RequestBody List<ExamPageSetParam> list) {
        return examPageSetService.saveExamPageSetPoint(examId,list);
    }
}