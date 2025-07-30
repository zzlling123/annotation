package com.xinkao.erp.summary.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentExamScoreDTO {

    @ExcelProperty("学号")
    private Integer userId;
    @ExcelProperty("姓名")
    private String userName;
    @ExcelProperty("考试成绩")
    private Integer totalScore;
}
