package com.xinkao.erp.exam.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.exam.excel.ExamPageSetImportModel;
import com.xinkao.erp.exam.mapper.ExamMapper;
import com.xinkao.erp.exam.param.ExamParam;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.service.ExamService;
import com.xinkao.erp.exam.vo.ExamDetailVo;
import com.xinkao.erp.exam.vo.ExamPageVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 考试表 服务实现类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@Service
public class ExamServiceImpl extends BaseServiceImpl<ExamMapper, Exam> implements ExamService {

    @Override
    public Page<ExamPageVo> page(ExamQuery query, Pageable pageable) {
        // TODO: 实现分页查询逻辑
        return null;
    }

    @Override
    public ExamDetailVo detail(Integer id) {
        // TODO: 实现查看考试详情逻辑
        return null;
    }

    @Override
    public BaseResponse<?> save(ExamParam examParam) {
        // TODO: 实现新增考试逻辑
        return null;
    }

    @Override
    public BaseResponse<?> update(ExamParam examParam) {
        // TODO: 实现编辑考试逻辑
        return null;
    }

    @Override
    public List<ExamPageSetImportModel> getExamPageSetByTypeAndShape(String examId) {
        // TODO: 实现删除考试逻辑
        return null;
    }
}