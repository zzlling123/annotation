package com.xinkao.erp.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class QuestionFormZipImportModel {

    @ExcelProperty("题目分类")
    private String type;                    // 如：图像标注、语音标注（后端转换到分类枚举/ID）

    @ExcelProperty("题目标题（自行输入）")
    private String title;

    @ExcelProperty("题干文件（文件路径）")
    private String questionFileRelPath;     // 题干文件相对路径（如：assets/题干/file.pdf）

    @ExcelProperty("题干素材（文件路径）")
    private String questionMaterialRelPath; // 题干素材相对路径（可选）

    @ExcelProperty("难度")
    private String difficultyLevel;         // 一级/二级/三级...

    @ExcelProperty("试题标签")
    private String symbol;                  // 学校/人社局（后端转枚举）

    @ExcelProperty("状态")
    private String state;                   // 启用/禁用（后端转枚举或0/1）

    @ExcelProperty("新增二级标题（题目，排序）")
    private String formTitleItemsRaw;       // 例：二级题目1, 1、二级题目2, 2

    @ExcelProperty("新增二级文字答案（标签，提示，答案，排序）多个作答框中间以“、”隔开")
    private String formAnswerItemsRaw;

    @ExcelProperty("新增二级文件答案（标签，类型示.png，文件路径，排序）多个作答框中间以“、”隔开")
    private String formFileAnswerItemsRaw;
}