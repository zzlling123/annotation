package com.xinkao.erp.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Getter;
import lombok.Setter;

/**
 * 题目批量导入模板
 */
@HeadRowHeight(20)
@Setter
@Getter
public class QuestionImportModel {

    @ExcelProperty(index = 0, value = "题目类型")
    @ColumnWidth(15)
    private String shape; // 100-单选 200-多选 300-填空 400-主观题 500-操作题

    @ExcelProperty(index = 1, value = "题目分类")
    @ColumnWidth(15)
    private String type;

    @ExcelProperty(index = 2, value = "题目标题（自行输入）")
    @ColumnWidth(30)
    private String title;

    @ExcelProperty(index = 3, value = "题目详情（自行输入）")
    @ColumnWidth(50)
    private String question;

    @ExcelProperty(index = 4, value = "题目难度")
    @ColumnWidth(15)
    private String difficultyLevel;


    @ExcelProperty(index = 5, value = "题目选项（以英文“%$%”为隔断）")
    @ColumnWidth(50)
    private String options;

    @ExcelProperty(index = 6, value = "答案（多选题以及如果有多个填空以中文“%$%”隔开）")
    @ColumnWidth(70)
    private String answer;

    @ExcelProperty(index = 7, value = "解析")
    @ColumnWidth(50)
    private String answerTip;

    @ExcelProperty(index = 8, value = "试题标签")
    @ColumnWidth(15)
    private String symbol; // 1-人社局 2-学校

    @ExcelProperty(index = 9, value = "状态")
    @ColumnWidth(15)
    private String state; // 0-否 1-是

    @ExcelProperty(index = 10, value = "是否需要批改（供主观题进行选择）")
    @ColumnWidth(30)
    private String needCorrect;

    @ExcelProperty(index = 11, value = "备注")
    @ColumnWidth(30)
    private String remark;





}