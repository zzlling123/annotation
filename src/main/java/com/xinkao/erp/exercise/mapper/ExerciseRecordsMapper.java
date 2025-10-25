package com.xinkao.erp.exercise.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.course.entity.Course;
import com.xinkao.erp.course.query.CourseQuery;
import com.xinkao.erp.exercise.entity.ExerciseRecords;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.exercise.query.ExerciseRecordsQuery;
import com.xinkao.erp.summary.param.SummaryStuParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExerciseRecordsMapper extends BaseMapper<ExerciseRecords> {
    Page<ExerciseRecords> page(Page page, ExerciseRecordsQuery query);
    List<ExerciseRecordsQuery> getListUserName(@Param("summaryStuParam") SummaryStuParam summaryStuParam);
}
