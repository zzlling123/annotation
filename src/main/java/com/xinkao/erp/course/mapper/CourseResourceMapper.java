package com.xinkao.erp.course.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.course.entity.CourseResource;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.course.query.CourseResourceQuery;
import com.xinkao.erp.manage.vo.ClassInfoVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 章节资源表 Mapper 接口
 * </p>
 *
 * @author zzl
 * @since 2025-03-21 17:19:23
 */
@Mapper
public interface CourseResourceMapper extends BaseMapper<CourseResource> {
    Page<CourseResource> page(Page page, CourseResourceQuery query);
}
