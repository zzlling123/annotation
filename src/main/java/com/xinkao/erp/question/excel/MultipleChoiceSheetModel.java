package com.xinkao.erp.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * 多选题 Sheet 实体
 * 预期表头：序号、题目分类、题目难度、所属范围、试题、选项A、选项B、选项C、选项D、选项E（可选）、答案
 * 答案支持多选，示例：AB、ACD、ABCDE
 */
@Data
@HeadRowHeight(20)
public class MultipleChoiceSheetModel {

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

    @ExcelProperty("选项E")
    @ColumnWidth(28)
    private String optionE;

    // 多选题答案：期望为如 "AB"、"ACD" 组合
    @ExcelProperty("答案")
    @ColumnWidth(12)
    private String answer;

    @ExcelProperty("知识点名称")
    @ColumnWidth(20)
    private String knowledgePointName;
} 