package com.xinkao.erp.exam.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.DataScope;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.exception.BusinessException;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.util.ExcelUtils;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.exam.excel.ExamPageSetImportErrorModel;
import com.xinkao.erp.exam.excel.ExamPageSetImportModel;
import com.xinkao.erp.exam.excel.ExamPageSetTypeModelListener;
import com.xinkao.erp.exam.param.ExamParam;
import com.xinkao.erp.exam.query.ExamQuery;
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
     * 导入试卷题目分布设置模板下载
     *
     */
    @PrimaryDataSource
    @DataScope(role = "1,2")
    @ApiOperation(value = "试卷题目分布设置模板")
    @RequestMapping(value = "/examTypeSetTemplate", method = RequestMethod.POST, produces = "application/octet-stream")
    public void examTypeSetTemplate(HttpServletResponse response, @RequestParam String examId) {
        // 从题库中按照 type 和 shape 获取各自题库数量
        List<ExamPageSetImportModel> list = examService.getExamPageSetByTypeAndShape(examId);
        try {
            ExcelUtils.writeExcel(response, list, "试卷题目分布设置模板", "试卷题目分布设置模板",
                    ExamPageSetImportModel.class);
        } catch (IOException e) {
            throw new BusinessException("导出试卷题目分布设置模板失败");
        }
    }

    // 导入试卷设置
    @PostMapping("/importExamPageSetPoint")
    @ApiOperation(value = "导入试卷设置")
    @Log(content = "导入试卷设置",isSaveResponseData = false,operationType = OperationType.IMPORT)
    public BaseResponse importExamPageSetPoint(HttpServletResponse response, @RequestParam(value="file") MultipartFile file, @RequestParam String examId) {
        String token = RandomUtil.randomString(20);
        redisUtil.set(token, "", 1, TimeUnit.HOURS);
        ExamPageSetTypeModelListener examPageSetPointModelListener =  new ExamPageSetTypeModelListener(response,token,examId);
        try {
            EasyExcel.read(file.getInputStream(), ExamPageSetImportModel.class, examPageSetPointModelListener).sheet().headRowNumber(1).doRead();
        } catch (IOException e) {
            throw new BusinessException("导入试卷设置失败");
        }
        BaseResponse baseResponse = BeanUtil.copyProperties(JSON.parseObject(redisUtil.get(token)),BaseResponse.class);
        if ("ok".equals(baseResponse.getState())){
            return BaseResponse.ok(baseResponse.getMsg());
        }else{
            return BaseResponse.other(token);
        }
    }

    /**
     * 下载错误文件
     * @return
     */
    @PostMapping("/getErrorExamPageSetPoint")
    @ApiOperation(value = "下载错误文件")
    public void getErrorClassSubjectImportExcel(HttpServletResponse response,@RequestParam String token) {
        JSONArray json = JSON.parseObject(redisUtil.get(token)).getJSONArray("data");
        List<ExamPageSetImportErrorModel> examPageSetImportErrorModelList = BeanUtil.copyToList(json, ExamPageSetImportErrorModel.class);
        //下载文件
        try {
            ExcelUtils.writeExcel(response, examPageSetImportErrorModelList, "错误试卷设置文件", "错误试卷设置文件",
                    ExamPageSetImportErrorModel.class);
        } catch (IOException e) {
            throw new BusinessException("导出错误试卷设置文件失败");
        }
    }
}