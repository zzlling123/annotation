package com.xinkao.erp.course.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.course.entity.CourseChapter;
import com.xinkao.erp.course.query.CourseChapterQuery;
import com.xinkao.erp.course.service.CourseChapterService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/course-chapter")
public class CourseChapterController {

    @Autowired
    private CourseChapterService courseChapterService;

    @GetMapping("/getList/{id}")
    @ApiOperation("根据课程id获取章节信息作为下拉列表")
    public BaseResponse<List<CourseChapter>> getList(@PathVariable Integer id) {
        return BaseResponse.ok(courseChapterService.lambdaQuery()
                .eq(CourseChapter::getIsDel, 0)
                .eq(CourseChapter::getCourseId, id)
                .list());
    }

    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询课程章节信息")
    public BaseResponse<Page<CourseChapter>> page(@Valid @RequestBody CourseChapterQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<CourseChapter> voPage = courseChapterService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    @PrimaryDataSource
    @PostMapping("/save")
    @ApiOperation("新增课程章节信息")
    @Log(content = "新增课程章节信息", operationType = OperationType.INSERT, isSaveRequestData = false)
    public BaseResponse<?> save(@Valid @RequestBody CourseChapter courseChapter) {
        return courseChapterService.save1(courseChapter);
    }

    @PostMapping("/update")
    @ApiOperation("编辑课程章节信息")
    @Log(content = "编辑课程章节信息", operationType = OperationType.UPDATE, isSaveRequestData = false)
    public BaseResponse<?> update(@Valid @RequestBody CourseChapter courseChapter) {
        return courseChapterService.update(courseChapter);
    }

    @PostMapping("/delete/{id}")
    @ApiOperation("删除课程章节信息")
    @Log(content = "删除课程章节信息", operationType = OperationType.DELETE, isSaveRequestData = false)
    public BaseResponse<?> delete(@PathVariable Integer id) {
        return courseChapterService.delete(id);
    }

}
