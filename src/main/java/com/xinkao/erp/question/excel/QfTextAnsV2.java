package com.xinkao.erp.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class QfTextAnsV2 {
    @ExcelProperty("组代码(group_code)")
    private String groupCode;
    @ExcelProperty("标题编号(title_no)")
    private Integer titleNo;
    @ExcelProperty("标签(label)")
    private String label;
    @ExcelProperty("提示(tip)")
    private String tip;
    @ExcelProperty("答案(answer)")
    private String answer;
    @ExcelProperty("排序(sort)")
    private Integer sort;
} 