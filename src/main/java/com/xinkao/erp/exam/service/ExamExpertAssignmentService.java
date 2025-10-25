package com.xinkao.erp.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinkao.erp.exam.entity.ExamExpertAssignment;

import java.util.List;

public interface ExamExpertAssignmentService extends IService<ExamExpertAssignment> {

    boolean assignExamToExperts(Integer examId);

    List<ExamExpertAssignment> getAssignmentsByExamId(Integer examId);

    List<ExamExpertAssignment> getAssignmentsByExpertId(Integer expertId);

    List<ExamExpertAssignment> getAssignmentsByExamIdAndExpertId(Integer examId, Integer expertId);
} 