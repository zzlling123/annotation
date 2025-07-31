package com.xinkao.erp.summary.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.exam.entity.ExamPageUserAnswer;
import com.xinkao.erp.exam.mapper.ExamMapper;
import com.xinkao.erp.exam.mapper.ExamPageUserAnswerMapper;
import com.xinkao.erp.summary.dto.ExamScoreDTO;
import com.xinkao.erp.summary.dto.StudentExamScoreDTO;
import com.xinkao.erp.summary.service.ExamScoreService;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExamScoreServiceImpl implements ExamScoreService {

    @Autowired
    private ExamPageUserAnswerMapper examPageUserAnswerMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ExamMapper examMapper;

    // 老师：导出班级单次考试成绩
    @Override
    public List<StudentExamScoreDTO> getClassExamScores(Integer classId, Integer examId) {
        // 1. 查询班级所有学生
        List<User> students = userMapper.selectList(
                new LambdaQueryWrapper<User>().eq(User::getClassId, classId)
        );
        if (students.isEmpty()) return Collections.emptyList();
        List<Integer> userIds = students.stream().map(User::getId).collect(Collectors.toList());

        // 2. 查询该考试下这些学生的所有作答
        List<ExamPageUserAnswer> answers = examPageUserAnswerMapper.selectList(
                new LambdaQueryWrapper<ExamPageUserAnswer>()
                        .eq(ExamPageUserAnswer::getExamId, examId)
                        .in(ExamPageUserAnswer::getUserId, userIds)
        );

        // 3. 按userId分组聚合
        Map<Integer, List<ExamPageUserAnswer>> groupByUser = answers.stream()
                .collect(Collectors.groupingBy(ExamPageUserAnswer::getUserId));

        List<StudentExamScoreDTO> result = new ArrayList<>();
        for (User student : students) {
            List<ExamPageUserAnswer> userAnswers = groupByUser.getOrDefault(student.getId(), Collections.emptyList());
            int totalScore = userAnswers.stream()
                    .mapToInt(a -> a.getUserScore() == null ? 0 : a.getUserScore())
                    .sum();
            StudentExamScoreDTO dto = new StudentExamScoreDTO();
            dto.setUserId(student.getId());
            dto.setUserName(student.getRealName());
            dto.setTotalScore(totalScore);
            result.add(dto);
        }
        return result;
    }

    @Override
    // 学生：导出自己多次考试成绩
    public List<ExamScoreDTO> getStudentExamScores(Integer userId) {
        // 1. 查询该学生所有作答
        List<ExamPageUserAnswer> answers = examPageUserAnswerMapper.selectList(
                new LambdaQueryWrapper<ExamPageUserAnswer>().eq(ExamPageUserAnswer::getUserId, userId)
        );

        // 2. 按examId分组聚合
        Map<Integer, List<ExamPageUserAnswer>> groupByExam = answers.stream()
                .collect(Collectors.groupingBy(ExamPageUserAnswer::getExamId));

        List<ExamScoreDTO> result = new ArrayList<>();
        for (Map.Entry<Integer, List<ExamPageUserAnswer>> entry : groupByExam.entrySet()) {
            Integer examId = entry.getKey();
            int totalScore = entry.getValue().stream()
                    .mapToInt(a -> a.getUserScore() == null ? 0 : a.getUserScore())
                    .sum();
            String examName = "";
            Exam exam = examMapper.selectById(examId);
            if (exam != null) {
                examName = exam.getExamName() ;
            }
            ExamScoreDTO dto = new ExamScoreDTO();
            dto.setExamId(examId);
            dto.setExamName(examName);
            dto.setTotalScore(totalScore);
            result.add(dto);
        }
        return result;
    }
}
