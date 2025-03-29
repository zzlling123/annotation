package com.xinkao.erp.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exam.param.ExamParam;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.service.ExamService;
import com.xinkao.erp.exam.vo.ExamDetailVo;
import com.xinkao.erp.exam.vo.ExamPageVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    /**
     * 分页查询考试信息
     *
     * @param query 查询条件
     * @return 分页结果
     */
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
    @PostMapping("/save")
    @ApiOperation("新增考试")
    public BaseResponse<?> save(@Valid @RequestBody ExamParam examParam) {
        return examService.save(examParam);
    }

    /**
     * 编辑考试
     *
     * @param examParam 考试信息
     * @return 操作结果
     */
    @PostMapping("/update")
    @ApiOperation("编辑考试")
    public BaseResponse<?> update(@Valid @RequestBody ExamParam examParam) {
        return examService.update(examParam);
    }

    /**
     * 导入试卷题目分布设置
     *
     */

    /**
     * 下载错误文件
     *
     */

    /**
     * 获取知识点分布列表
     *
     * @return
     */
}