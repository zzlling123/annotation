package com.xinkao.erp.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
public class ImportResultErrorRow {
    @ExcelProperty("行号")
    @ColumnWidth(10)
    private Integer rowNum;

    @ExcelProperty("错误信息")
    @ColumnWidth(48)
    private String message;
} 