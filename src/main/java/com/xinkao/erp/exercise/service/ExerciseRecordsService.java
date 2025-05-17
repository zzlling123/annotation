package com.xinkao.erp.exercise.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exercise.entity.ExerciseRecords;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.exercise.query.ExerciseRecordsQuery;
import com.xinkao.erp.summary.param.SummaryStuParam;

import java.util.List;

/**
 * <p>
 * 练习记录表 服务类
 * </p>
 *
 * @author zzl
 * @since 2025-04-05 23:15:56
 */
public interface ExerciseRecordsService extends BaseService<ExerciseRecords> {
    Page<ExerciseRecords> page(ExerciseRecordsQuery query, Pageable pageable);
    BaseResponse<?> save1(ExerciseRecords exerciseRecords);
    BaseResponse<?> update(ExerciseRecords exerciseRecords);
    BaseResponse<?> delete(Integer id);
    ExerciseRecords detailById(ExerciseRecords exerciseRecords);
    List<ExerciseRecordsQuery> getListUserName(SummaryStuParam summaryStuParam);
}
