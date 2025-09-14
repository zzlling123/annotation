package com.xinkao.erp.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * 判断题 Sheet 实体
 * 预期表头：序号、题目分类、题目难度、所属范围、试题、对/错
 * 其中“对/错”列填写 A/B 或 对/错（后续解析时统一转换）。
 */
@Data
@HeadRowHeight(20)
public class TrueFalseSheetModel {

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

    // 判断题答案：A/对 表示正确；B/错 表示错误
    @ExcelProperty("对/错")
    @ColumnWidth(10)
    private String answer;

    @ExcelProperty("知识点名称")
    @ColumnWidth(20)
    private String knowledgePointName;
}