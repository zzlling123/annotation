package com.xinkao.erp.summary.service;

import com.xinkao.erp.summary.dto.ExamScoreDTO;
import com.xinkao.erp.summary.dto.StudentExamScoreDTO;

import java.util.List;

public interface ExamScoreService {
    public List<StudentExamScoreDTO> getClassExamScores(Integer classId, Integer examId);
    public List<ExamScoreDTO> getStudentExamScores(Integer userId);
}
