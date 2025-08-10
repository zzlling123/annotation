package com.xinkao.erp.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class QfFileAnsV2 {
    @ExcelProperty("组代码(group_code)")
    private String groupCode;
    @ExcelProperty("标题编号(title_no)")
    private Integer titleNo;
    @ExcelProperty("标签(label)")
    private String label;
    @ExcelProperty("类型(fileType)")
    private String fileType;
    @ExcelProperty("文件相对路径(fileRelPath)")
    private String fileRelPath;
    @ExcelProperty("排序(sort)")
    private Integer sort;
} 