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

@RestController
@RequestMapping("/instant-feedbacks")
public class InstantFeedbacksController {

    @Autowired
    private InstantFeedbacksService instantFeedbacksService;
    @PrimaryDataSource
    @PostMapping("/page")
    public BaseResponse<Page<InstantFeedbacks>> page(@Valid @RequestBody InstantFeedbacksQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<InstantFeedbacks> voPage = instantFeedbacksService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    @PrimaryDataSource
    @PostMapping("/save")
    @Log(content = "添加练习记录表，提交练习答案", operationType = OperationType.INSERT, isSaveRequestData = false)
    public BaseResponse<?> save(@Valid @RequestBody InstantFeedbacks instantFeedbacks) {
        return instantFeedbacksService.save1(instantFeedbacks);
    }

    @PostMapping("/update")
    @Log(content = "更新练习记录表", operationType = OperationType.UPDATE, isSaveRequestData = false)
    public BaseResponse<?> update(@Valid @RequestBody InstantFeedbacks instantFeedbacks) {
        if (instantFeedbacks.getId() == null) {
            return BaseResponse.fail("练习记录ID不能为空");
        }
        return instantFeedbacksService.update(instantFeedbacks);
    }

    @PostMapping("/delete/{id}")
    @Log(content = "删除练习记录表", operationType = OperationType.DELETE, isSaveRequestData = false)
    public BaseResponse<?> delete(@PathVariable Integer id) {
        return instantFeedbacksService.delete(id);
    }
}
