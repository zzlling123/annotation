package com.xinkao.erp.course.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.course.entity.Course;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.course.query.CourseQuery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    Page<Course> page(Page page, CourseQuery query);
}
