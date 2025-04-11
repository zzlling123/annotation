package com.xinkao.erp.exercise.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.exercise.entity.ExerciseRecords;
import com.xinkao.erp.exercise.entity.InstantFeedbacks;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.exercise.query.ExerciseRecordsQuery;
import com.xinkao.erp.exercise.query.InstantFeedbacksQuery;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 即时反馈表 Mapper 接口
 * </p>
 *
 * @author zzl
 * @since 2025-04-05 23:15:56
 */
@Mapper
public interface InstantFeedbacksMapper extends BaseMapper<InstantFeedbacks> {
    Page<InstantFeedbacks> page(Page page, InstantFeedbacksQuery query);

}
