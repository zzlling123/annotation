package com.xinkao.erp.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exam.entity.ExamExpert;
import com.xinkao.erp.exam.vo.ExamPageVo;

import java.util.List;

public interface ExamExpertService extends IService<ExamExpert> {

    List<ExamExpert> getExpertsByExamId(Integer examId);

    List<ExamExpert> getExamsByExpertId(Integer expertId);

    public Page<ExamPageVo> getExamByExamId(List<Integer> examIds, Pageable pageable);
} 