package com.xinkao.erp.course.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.course.entity.Course;
import com.xinkao.erp.course.entity.CourseChapter;
import com.xinkao.erp.course.query.CourseChapterQuery;
import com.xinkao.erp.course.query.CourseQuery;
import com.xinkao.erp.course.service.CourseChapterService;
import com.xinkao.erp.course.service.CourseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 课程表	 前端控制器
 *
 * @author zzl
 * @since 2025-03-21 17:19:23
 */
@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    /**
     * 获取课程信息作为下拉列表
     */
    @GetMapping("/getList")
    @ApiOperation("获取课程信息作为下拉列表")
    public BaseResponse<List<Course>> getList() {
        return BaseResponse.ok(courseService.lambdaQuery().list());
    }

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询课程信息")
    public BaseResponse<Page<Course>> page(@Valid @RequestBody CourseQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<Course> voPage = courseService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    /**
     * 新增课程章节信息
     *
     * @param course 课程章节信息参数
     * @return 操作结果
     */
    @PrimaryDataSource
    @PostMapping(value = "/save",consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation("新增课程信息")
    @Log(content = "新增课程信息", operationType = OperationType.INSERT, isSaveRequestData = false)
    public BaseResponse<?> save(@Valid @ModelAttribute Course course,@RequestParam(name = "coverImageFile", required = false) MultipartFile coverImage) {
        return courseService.save1(course,coverImage);
    }

    /**
     * 编辑课程章节信息
     *
     * @param course 课程章节信息参数
     * @return 操作结果
     */
    @PostMapping(value = "/update",consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation("编辑课程信息")
    @Log(content = "编辑课程信息", operationType = OperationType.UPDATE, isSaveRequestData = false)
    public BaseResponse<?> update(@Valid @ModelAttribute Course course,@RequestParam(name = "coverImageFile", required = false) MultipartFile coverImage) {
        return courseService.update(course,coverImage);
    }

    /**
     * 删除课程章节信息
     *
     * @param id 课程章节ID
     * @return 操作结果
     */
    @PostMapping("/delete/{id}")
    @ApiOperation("删除课程信息")
    @Log(content = "删除课程信息", operationType = OperationType.DELETE, isSaveRequestData = false)
    public BaseResponse<?> delete(@PathVariable Integer id) {
        return courseService.delete(id);
    }

    @PrimaryDataSource
    @PostMapping("/updateState")
    @ApiOperation("修改课程状态")
    public BaseResponse<?> updateState(@RequestBody UpdateStateParam param) {
        return courseService.updateState(param);
    }
}
