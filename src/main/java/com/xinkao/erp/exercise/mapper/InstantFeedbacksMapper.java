package com.xinkao.erp.exercise.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.exercise.entity.ExerciseRecords;
import com.xinkao.erp.exercise.entity.InstantFeedbacks;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.exercise.query.ExerciseRecordsQuery;
import com.xinkao.erp.exercise.query.InstantFeedbacksQuery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InstantFeedbacksMapper extends BaseMapper<InstantFeedbacks> {
    Page<InstantFeedbacks> page(Page page, InstantFeedbacksQuery query);

}
