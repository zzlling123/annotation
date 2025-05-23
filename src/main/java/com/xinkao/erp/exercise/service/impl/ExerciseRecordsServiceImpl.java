package com.xinkao.erp.exercise.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exercise.entity.ExerciseRecords;
import com.xinkao.erp.exercise.entity.InstantFeedbacks;
import com.xinkao.erp.exercise.mapper.ExerciseRecordsMapper;
import com.xinkao.erp.exercise.query.ExerciseRecordsQuery;
import com.xinkao.erp.exercise.service.ExerciseRecordsService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.summary.param.SummaryStuParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 练习记录表 服务实现类
 * </p>
 *
 * @author zzl
 * @since 2025-04-05 23:15:56
 */
@Service
public class ExerciseRecordsServiceImpl extends BaseServiceImpl<ExerciseRecordsMapper, ExerciseRecords> implements ExerciseRecordsService {

    @Autowired
    private ExerciseRecordsMapper exerciseRecordsMapper;

    @Override
    public Page<ExerciseRecords> page(ExerciseRecordsQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return exerciseRecordsMapper.page(page, query);
    }

    /**
     * 通过id查询练习记录详情
     * @param exerciseRecords
     * @return
     */
    public ExerciseRecords detailById(ExerciseRecords exerciseRecords) {
        return getById(exerciseRecords.getId());
    }

    @Override
    public List<ExerciseRecordsQuery> getListUserName(SummaryStuParam summaryStuParam) {
        return exerciseRecordsMapper.getListUserName(summaryStuParam);
    }

    @Override
    public BaseResponse<?> save1(ExerciseRecords exerciseRecords) {
        return save(exerciseRecords) ? BaseResponse.ok("新增成功！") : BaseResponse.fail("新增失败！");
    }

    @Override
    public BaseResponse<?> update(ExerciseRecords exerciseRecords) {
        return updateById(exerciseRecords) ? BaseResponse.ok("更新成功！") : BaseResponse.fail("更新失败！");
    }

    @Override
    public BaseResponse<?> delete(Integer id) {
        return lambdaUpdate().eq(ExerciseRecords::getId, id).set(ExerciseRecords::getIsDel, CommonEnum.IS_DEL.YES.getCode()).update() ? BaseResponse.ok("删除成功！") : BaseResponse.fail("删除失败！");
    }
}
