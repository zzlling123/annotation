package com.xinkao.erp.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinkao.erp.exam.entity.ExamExpertAssignment;

import java.util.List;

public interface ExamExpertAssignmentService extends IService<ExamExpertAssignment> {
    
    /**
     * 为考试分配专家判卷任务
     * @param examId 考试ID
     * @return 分配结果
     */
    boolean assignExamToExperts(Integer examId);
    
    /**
     * 根据考试ID查询分配情况
     * @param examId 考试ID
     * @return 分配列表
     */
    List<ExamExpertAssignment> getAssignmentsByExamId(Integer examId);
    
    /**
     * 根据专家ID查询分配情况
     * @param expertId 专家ID
     * @return 分配列表
     */
    List<ExamExpertAssignment> getAssignmentsByExpertId(Integer expertId);
} 