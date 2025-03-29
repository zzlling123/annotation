package com.xinkao.erp.course.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.course.entity.CourseChapter;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.course.query.CourseChapterQuery;

/**
 * <p>
 * 课程章节表 服务类
 * </p>
 *
 * @author zzl
 * @since 2025-03-21 17:19:23
 */
public interface CourseChapterService extends BaseService<CourseChapter> {
    Page<CourseChapter> page(CourseChapterQuery query, Pageable pageable);
    BaseResponse<?> save1(CourseChapter courseChapter);
    BaseResponse<?> update(CourseChapter courseChapter);
    BaseResponse<?> delete(Integer id);
}
