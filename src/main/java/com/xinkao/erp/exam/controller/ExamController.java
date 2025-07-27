package com.xinkao.erp.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.DataScope;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.exam.entity.ExamPageSetType;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 考试管理
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
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

    /**
     * 分页查询考试信息
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询考试信息")
    public BaseResponse<Page<ExamPageVo>> page(@RequestBody ExamQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<ExamPageVo> voPage = examService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    /**
     * 查看考试详情
     *
     * @param id 考试ID
     * @return 考试详情
     */
    @PrimaryDataSource
    @DataScope(role = "1,2")
    @GetMapping("/detail/{id}")
    @ApiOperation("查看考试详情")
    public BaseResponse<ExamDetailVo> detail(@PathVariable Integer id) {
        ExamDetailVo examDetailVo = examService.detail(id);
        return BaseResponse.ok(examDetailVo);
    }

    /**
     * 新增考试
     *
     * @param examParam 考试信息
     * @return 操作结果
     */
    @PrimaryDataSource
    @DataScope(role = "1,2")
    @PostMapping("/save")
    @ApiOperation("新增考试")
    @Log(content = "新增考试",operationType = OperationType.INSERT)
    public BaseResponse<?> save(@Valid @RequestBody ExamParam examParam) {
        return examService.save(examParam);
    }

    /**
     * 编辑考试
     *
     * @param examParam 考试信息
     * @return 操作结果
     */
    @PrimaryDataSource
    @DataScope(role = "1,2")
    @PostMapping("/update")
    @ApiOperation("编辑考试")
    @Log(content = "编辑考试",operationType = OperationType.UPDATE)
    public BaseResponse<?> update(@Valid @RequestBody ExamParam examParam) {
        return examService.update(examParam);
    }

    /**
     * 删除考试
     *
     * @param id 考试信息
     * @return 操作结果
     */
    @PrimaryDataSource
    @DataScope(role = "1,2")
    @PostMapping("/del/{id}")
    @ApiOperation("删除考试")
    @Log(content = "删除考试",operationType = OperationType.DELETE)
    public BaseResponse<?> del(@PathVariable Integer id) {
        return examService.del(id);
    }

    /**
     * 获取试卷题目分布
     *
     */
    @PrimaryDataSource
    @DataScope(role = "1,2")
    @ApiOperation(value = "获取试卷题目分布")
    @PostMapping("/getExamTypeSet")
    public BaseResponse<List<ExamPageSetVo>> getExamTypeSet(@RequestParam String examId) {
        return BaseResponse.ok(examService.getExamPageSetByTypeAndShape(examId));
    }

    // 编辑试卷设置
    @PrimaryDataSource
    @ApiOperation(value = "编辑试卷设置")
    @PostMapping("/saveExamPageSetPoint/{examId}")
    @Log(content = "编辑试卷设置",isSaveResponseData = false,operationType = OperationType.INSERT)
    public BaseResponse<?> saveExamPageSetPoint(@PathVariable String examId,@RequestBody List<ExamPageSetParam> list) {
        return examPageSetService.saveExamPageSetPoint(examId,list);
    }
}