package com.xinkao.erp.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;


@Data
@HeadRowHeight(20)
public class SingleChoiceSheetModel {

    @ExcelProperty("序号")
    @ColumnWidth(8)
    private Integer index;

    @ExcelProperty("题目分类")
    @ColumnWidth(15)
    private String type;

    @ExcelProperty("题目难度")
    @ColumnWidth(12)
    private String difficultyLevel;

    @ExcelProperty("所属范围")
    @ColumnWidth(12)
    private String symbol;

    @ExcelProperty("试题")
    @ColumnWidth(60)
    private String question;

    @ExcelProperty("选项A")
    @ColumnWidth(28)
    private String optionA;

    @ExcelProperty("选项B")
    @ColumnWidth(28)
    private String optionB;

    @ExcelProperty("选项C")
    @ColumnWidth(28)
    private String optionC;

    @ExcelProperty("选项D")
    @ColumnWidth(28)
    private String optionD;

    @ExcelProperty("答案")
    @ColumnWidth(10)
    private String answer;

    @ExcelProperty("知识点名称")
    @ColumnWidth(20)
    private String knowledgePointName;
} 