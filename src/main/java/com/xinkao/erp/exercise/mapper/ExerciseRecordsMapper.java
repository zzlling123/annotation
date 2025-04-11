package com.xinkao.erp.exercise.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.course.entity.Course;
import com.xinkao.erp.course.query.CourseQuery;
import com.xinkao.erp.exercise.entity.ExerciseRecords;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.exercise.query.ExerciseRecordsQuery;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 练习记录表 Mapper 接口
 * </p>
 *
 * @author zzl
 * @since 2025-04-05 23:15:56
 */
@Mapper
public interface ExerciseRecordsMapper extends BaseMapper<ExerciseRecords> {
    Page<ExerciseRecords> page(Page page, ExerciseRecordsQuery query);
}
