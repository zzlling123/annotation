package com.xinkao.erp.course.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.course.entity.CourseChapter;
import com.xinkao.erp.course.entity.CourseResource;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.course.query.CourseChapterQuery;
import com.xinkao.erp.course.query.CourseResourceQuery;

import java.io.IOException;
import java.util.List;

public interface CourseResourceService extends BaseService<CourseResource> {
    void convertToHLS(String inputPath, String outputDir) throws IOException;
    List<CourseResource> getListByChapterId(Long chapterId);
    Page<CourseResource> page(CourseResourceQuery query, Pageable pageable);
    BaseResponse<?> save1(CourseResource courseResource);
    BaseResponse<?> update(CourseResource courseResource);
    BaseResponse<?> delete(Integer id);
}
