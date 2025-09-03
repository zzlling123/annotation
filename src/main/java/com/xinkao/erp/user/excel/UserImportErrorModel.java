package com.xinkao.erp.user.excel;

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
public class UserImportErrorModel extends BaseExcelModel {

    @ExcelProperty(value = "姓名", index = 0)
    @Excel(prompt = "必填")
    @ColumnWidth(20)
    private String realName;

    @ExcelProperty(value = "身份证号", index = 1)
    @Excel(prompt = "必填")
    @ColumnWidth(20)
    private String idCard;

    @ExcelProperty(value = "手机号", index = 2)
    @Excel(prompt = "必填")
    @ColumnWidth(20)
    private String mobile;

    @ExcelProperty(value = "班级名称", index = 3)
    @Excel(prompt = "学生和社会考生必填，其他角色可选")
    @ColumnWidth(20)
    private String className;

    @ExcelProperty(value = "角色名称", index = 4)
    @Excel(prompt = "必填")
    @ColumnWidth(20)
    private String roleName;

    @ExcelProperty(value = "性别", index = 5)
    @Excel(prompt = "必填")
    @ColumnWidth(20)
    private String sex;

    @ExcelProperty(value = "错误信息", index = 6)
    @ColumnWidth(20)
    private String errorInfo;
}
