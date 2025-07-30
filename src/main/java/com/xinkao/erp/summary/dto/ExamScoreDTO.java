package com.xinkao.erp.summary.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExamScoreDTO {
    @ExcelProperty("考试ID")
    private Integer examId;
    @ExcelProperty("考试名称")
    private String examName;
    @ExcelProperty("考试成绩")
    private Integer totalScore;
}
