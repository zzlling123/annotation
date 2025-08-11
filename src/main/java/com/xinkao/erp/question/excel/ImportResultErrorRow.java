package com.xinkao.erp.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ImportResultErrorRow {
    @ExcelProperty("行号")
    private Integer rowNum;

    @ExcelProperty("错误信息")
    private String message;
} 