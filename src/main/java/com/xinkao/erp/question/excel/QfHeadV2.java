package com.xinkao.erp.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class QfHeadV2 {
    @ExcelProperty("组代码(group_code)")
    private String groupCode;
    @ExcelProperty("题目分类(type)")
    private String type;
    @ExcelProperty("题目标题(title)")
    private String title;
    @ExcelProperty("题干文件相对路径(questionFileRelPath)")
    private String questionFileRelPath;
    @ExcelProperty("题干素材相对路径(questionMaterialRelPath)")
    private String questionMaterialRelPath;
    @ExcelProperty("难度(difficultyLevel)")
    private String difficultyLevel;
    @ExcelProperty("试题标签(symbol)")
    private String symbol;
    @ExcelProperty("状态(state)")
    private String state;
    @ExcelProperty("知识点名称(knowledgePointName)")
    private String knowledgePointName;
    @ExcelProperty("备注(remark)")
    private String remark;
} 