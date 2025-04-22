package com.xinkao.erp.exercise.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.course.entity.CourseChapter;
import com.xinkao.erp.course.query.CourseChapterQuery;
import com.xinkao.erp.exercise.entity.InstantFeedbacks;
import com.xinkao.erp.exercise.query.InstantFeedbacksQuery;
import com.xinkao.erp.exercise.service.InstantFeedbacksService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 即时反馈表 前端控制器
 *
 * @author zzl
 * @since 2025-04-05 23:15:56
 */
@RestController
@RequestMapping("/instant-feedbacks")
public class InstantFeedbacksController {

    @Autowired
    private InstantFeedbacksService instantFeedbacksService;
    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询即时反馈表")
    public BaseResponse<Page<InstantFeedbacks>> page(@Valid @RequestBody InstantFeedbacksQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<InstantFeedbacks> voPage = instantFeedbacksService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    /**
     * 新增练习记录表信息
     *
     * @param instantFeedbacks 练习记录表信息参数
     * @return 操作结果
     */
    @PrimaryDataSource
    @PostMapping("/save")
    @ApiOperation("添加练习记录表 提交练习答案")
    @Log(content = "添加练习记录表，提交练习答案", operationType = OperationType.INSERT, isSaveRequestData = false)
    public BaseResponse<?> save(@Valid @RequestBody InstantFeedbacks instantFeedbacks) {
        return instantFeedbacksService.save1(instantFeedbacks);
    }

    /**
     * 编辑练习记录表信息
     *
     * @param instantFeedbacks 练习记录表
     * @return 操作结果
     */
    @PostMapping("/update")
    @ApiOperation("更新练习记录表")
    @Log(content = "更新练习记录表", operationType = OperationType.UPDATE, isSaveRequestData = false)
    public BaseResponse<?> update(@Valid @RequestBody InstantFeedbacks instantFeedbacks) {
        if (instantFeedbacks.getId() == null) {
            return BaseResponse.fail("练习记录ID不能为空");
        }
        return instantFeedbacksService.update(instantFeedbacks);
    }

    /**
     * 删除练习记录表信息
     *
     * @param id 课程章节ID
     * @return 操作结果
     */
    @PostMapping("/delete/{id}")
    @ApiOperation("删除练习记录表")
    @Log(content = "删除练习记录表", operationType = OperationType.DELETE, isSaveRequestData = false)
    public BaseResponse<?> delete(@PathVariable Integer id) {
        return instantFeedbacksService.delete(id);
    }
}
