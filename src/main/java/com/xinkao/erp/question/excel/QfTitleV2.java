package com.xinkao.erp.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class QfTitleV2 {
    @ExcelProperty("组代码(group_code)")
    private String groupCode;
    @ExcelProperty("标题编号(title_no)")
    private Integer titleNo;
    @ExcelProperty("标题(title)")
    private String title;
    @ExcelProperty("排序(sort)")
    private Integer sort;
} 