package com.xinkao.erp.summary.controller;

import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.summary.dto.ExamScoreDTO;
import com.xinkao.erp.summary.dto.StudentExamScoreDTO;
import com.xinkao.erp.summary.service.ExamScoreService;
import com.xinkao.erp.summary.utils.ExcelExportUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/exam/score/export")
public class ExamScoreExportController {

    @Autowired
    private ExamScoreService examScoreService;

    // 老师导出班级单次考试成绩
    @GetMapping("/class-exam")
    public void exportClassExamScores(@RequestParam Integer classId, @RequestParam Integer examId, HttpServletResponse response) throws IOException {
        List<StudentExamScoreDTO> data = examScoreService.getClassExamScores(classId, examId);
        ExcelExportUtil.writeExcel(data, StudentExamScoreDTO.class, response, "班级考试成绩");
    }

    // 学生导出自己多次考试成绩
    @GetMapping("/student-exams")
    public void exportStudentExamScores(@RequestParam Integer userId, HttpServletResponse response) throws IOException {
        List<ExamScoreDTO> data = examScoreService.getStudentExamScores(userId);
        ExcelExportUtil.writeExcel(data, ExamScoreDTO.class, response, "我的考试成绩");
    }

    /**
     * 查询成绩（老师/学生通用）
     * 老师查班级单次考试：传classId和examId
     * 学生查自己多次考试：传userId
     */
    @GetMapping("/query")
    @PrimaryDataSource
    @ApiOperation(value = "查询成绩")
    public Object queryExamScores(
            @RequestParam(required = false) Integer classId,
            @RequestParam(required = false) Integer examId,
            @RequestParam(required = false) Integer userId) {

        //根据登录用户判断是否是老师还是学生
        boolean isTeacher = true;
        if (isTeacher && classId != null && examId != null) {
            return examScoreService.getClassExamScores(classId, examId);
        }else if (!isTeacher && userId != null){
            return examScoreService.getStudentExamScores(userId);
        }else{
            return "参数错误：请传入classId+examId（老师）或userId（学生）";
        }
    }
}
