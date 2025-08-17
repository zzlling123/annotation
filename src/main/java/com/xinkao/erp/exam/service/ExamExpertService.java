package com.xinkao.erp.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exam.entity.ExamExpert;
import com.xinkao.erp.exam.vo.ExamPageVo;

import java.util.List;

public interface ExamExpertService extends IService<ExamExpert> {
    
    /**
     * 根据考试ID查询专家列表
     */
    List<ExamExpert> getExpertsByExamId(Integer examId);
    
    /**
     * 根据专家ID查询考试列表
     */
    List<ExamExpert> getExamsByExpertId(Integer expertId);

    public Page<ExamPageVo> getExamByExamId(List<Integer> examIds, Pageable pageable);
} 