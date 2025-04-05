package com.xinkao.erp.exam.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.xinkao.erp.common.annotation.Excel;
import com.xinkao.erp.common.model.BaseExcelModel;
import lombok.Getter;
import lombok.Setter;

/**
 * 调班导入模板
 * @ClassName StuUpdateClassImportModel
 * @Description
 * @Author Ldy
 * @Date 2022/06/25 15:38
 **/
@HeadRowHeight(20)
@Setter
@Getter
public class ExamPageSetImportModel extends BaseExcelModel {

    //单选题、多选题、填空题、问答题、实践题
    @ExcelProperty(index = 0,value = "题目分类")
    @ColumnWidth(20)
    private String type;

    @ExcelProperty(index = 1,value = "单选题库题数")
    @ColumnWidth(20)
    private String choiceSingleCount = "0";

    @ExcelProperty(index = 2,value = "单选抽取题数")
    @ColumnWidth(20)
    private String choiceSingleChouCount;

    @ExcelProperty(index = 3,value = "单选得分")
    @ColumnWidth(20)
    private String choiceSingleScore;

    @ExcelProperty(index = 4,value = "多选题库题数")
    @ColumnWidth(20)
    private String choiceMultiCount = "0";

    @ExcelProperty(index = 5,value = "多选抽取题数")
    @ColumnWidth(20)
    private String choiceMultiChouCount;

    @ExcelProperty(index = 6,value = "多选得分")
    @ColumnWidth(20)
    private String choiceMultiScore;

    @ExcelProperty(index = 7,value = "多选部分得分")
    @ColumnWidth(20)
    private String choiceMultiPerPart;

    @ExcelProperty(index = 8,value = "填空题库题数")
    @ColumnWidth(20)
    private String choiceFillCount = "0";

    @ExcelProperty(index = 9,value = "填空抽取题数")
    @ColumnWidth(20)
    private String choiceFillChouCount;

    @ExcelProperty(index = 10,value = "填空得分")
    @ColumnWidth(20)
    private String choiceFillScore;

    @ExcelProperty(index = 11,value = "问答题库题数")
    @ColumnWidth(20)
    private String choiceAnswerCount = "0";

    @ExcelProperty(index = 12,value = "问答抽取题数")
    @ColumnWidth(20)
    private String choiceAnswerChouCount;

    @ExcelProperty(index = 13,value = "问答得分")
    @ColumnWidth(20)
    private String choiceAnswerScore;

    @ExcelProperty(index = 14,value = "实践题库题数")
    @ColumnWidth(20)
    private String choicePracticeCount = "0";

    @ExcelProperty(index = 15,value = "实践抽取题数")
    @ColumnWidth(20)
    private String choicePracticeChouCount;

    @ExcelProperty(index = 16,value = "实践得分")
    @ColumnWidth(20)
    private String choicePracticeScore;
}
