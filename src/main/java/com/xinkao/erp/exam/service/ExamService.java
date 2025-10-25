package com.xinkao.erp.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.exam.excel.ExamPageSetVo;
import com.xinkao.erp.exam.param.ExamParam;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.vo.ExamDetailVo;
import com.xinkao.erp.exam.vo.ExamPageVo;

import java.util.List;

public interface ExamService extends BaseService<Exam> {

    Page<ExamPageVo> page(ExamQuery query, Pageable pageable);

    ExamDetailVo detail(Integer id);

    BaseResponse<?> save(ExamParam examParam);

    BaseResponse<?> update(ExamParam examParam);

    BaseResponse<?> del(Integer id);

    List<ExamPageSetVo> getExamPageSetByTypeAndShape(String examId);
}