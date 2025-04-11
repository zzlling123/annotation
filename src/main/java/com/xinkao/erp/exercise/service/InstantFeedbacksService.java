package com.xinkao.erp.exercise.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exercise.entity.ExerciseRecords;
import com.xinkao.erp.exercise.entity.InstantFeedbacks;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.exercise.query.ExerciseRecordsQuery;
import com.xinkao.erp.exercise.query.InstantFeedbacksQuery;

/**
 * <p>
 * 即时反馈表 服务类
 * </p>
 *
 * @author zzl
 * @since 2025-04-05 23:15:56
 */
public interface InstantFeedbacksService extends BaseService<InstantFeedbacks> {
    Page<InstantFeedbacks> page(InstantFeedbacksQuery query, Pageable pageable);
    BaseResponse<?> save1(InstantFeedbacks instantFeedbacks);
    BaseResponse<?> update(InstantFeedbacks instantFeedbacks);
    BaseResponse<?> delete(Integer id);
}
