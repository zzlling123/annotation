package com.xinkao.erp.course.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.course.entity.CourseResource;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.course.query.CourseResourceQuery;
import com.xinkao.erp.manage.vo.ClassInfoVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseResourceMapper extends BaseMapper<CourseResource> {
    Page<CourseResource> page(Page page, CourseResourceQuery query);
}
