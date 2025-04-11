package com.xinkao.erp.course.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.course.entity.Course;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.course.query.CourseQuery;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 课程表	 Mapper 接口
 * </p>
 *
 * @author zzl
 * @since 2025-03-21 16:53:36
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    Page<Course> page(Page page, CourseQuery query);
}
