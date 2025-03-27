package com.xinkao.erp.course.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.course.entity.CourseChapter;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.course.query.CourseChapterQuery;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 课程章节表 Mapper 接口
 * </p>
 *
 * @author zzl
 * @since 2025-03-21 17:19:23
 */
@Mapper
public interface CourseChapterMapper extends BaseMapper<CourseChapter> {

    Page<CourseChapter> page(Page page, CourseChapterQuery query);
}
