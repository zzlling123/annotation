package com.xinkao.erp.course.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.course.entity.CourseChapter;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.course.query.CourseChapterQuery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseChapterMapper extends BaseMapper<CourseChapter> {

    Page<CourseChapter> page(Page page, CourseChapterQuery query);
}
