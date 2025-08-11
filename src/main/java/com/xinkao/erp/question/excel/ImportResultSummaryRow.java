package com.xinkao.erp.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ImportResultSummaryRow {
    @ExcelProperty("总数")
    private Integer totalCount;

    @ExcelProperty("成功")
    private Integer successCount;

    @ExcelProperty("失败")
    private Integer failCount;
} 