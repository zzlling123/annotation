package com.xinkao.erp.exercise.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.course.entity.CourseChapter;
import com.xinkao.erp.exercise.entity.InstantFeedbacks;
import com.xinkao.erp.exercise.mapper.InstantFeedbacksMapper;
import com.xinkao.erp.exercise.query.InstantFeedbacksQuery;
import com.xinkao.erp.exercise.service.InstantFeedbacksService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 即时反馈表 服务实现类
 * </p>
 *
 * @author zzl
 * @since 2025-04-05 23:15:56
 */
@Service
public class InstantFeedbacksServiceImpl extends BaseServiceImpl<InstantFeedbacksMapper, InstantFeedbacks> implements InstantFeedbacksService {
    @Autowired
    private InstantFeedbacksMapper instantFeedbacksMapper;

    @Override
    public Page<InstantFeedbacks> page(InstantFeedbacksQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return instantFeedbacksMapper.page(page, query);
    }

    @Override
    public BaseResponse<?> save1(InstantFeedbacks instantFeedbacks) {
        return save(instantFeedbacks) ? BaseResponse.ok("新增成功！") : BaseResponse.fail("新增失败！");
    }

    @Override
    public BaseResponse<?> update(InstantFeedbacks instantFeedbacks) {
        return updateById(instantFeedbacks) ? BaseResponse.ok("更新成功！") : BaseResponse.fail("更新失败！");
    }

    @Override
    public BaseResponse<?> delete(Integer id) {
        return lambdaUpdate().eq(InstantFeedbacks::getId, id).set(InstantFeedbacks::getIsDel, CommonEnum.IS_DEL.YES.getCode()).update() ? BaseResponse.ok("删除成功！") : BaseResponse.fail("删除失败！");
    }
}
