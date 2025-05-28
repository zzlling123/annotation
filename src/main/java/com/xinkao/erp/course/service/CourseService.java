package com.xinkao.erp.course.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.course.entity.Course;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.course.entity.CourseChapter;
import com.xinkao.erp.course.query.CourseChapterQuery;
import com.xinkao.erp.course.query.CourseQuery;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 课程表	 服务类
 * </p>
 *
 * @author zzl
 * @since 2025-03-21 16:53:36
 */
public interface CourseService extends BaseService<Course> {
    Page<Course> page(CourseQuery query, Pageable pageable);
    BaseResponse<?> save1(Course course, MultipartFile coverImage);
    BaseResponse<?> update(Course course, MultipartFile coverImage);
    BaseResponse<?> delete(Integer id);
    BaseResponse<?> updateState(UpdateStateParam param);
}
