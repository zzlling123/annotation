package com.xinkao.erp.user.excel;

import java.util.Date;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.xinkao.erp.common.model.BaseExcelModel;

import lombok.Data;

/**
 * 账号信息导出字段
 * @author hys_thanks
 */
@Data
public class UserExportModel extends BaseExcelModel{

	@ExcelProperty(index = 0,value = "用户姓名")
	private String realName;
	
	@ExcelProperty(index = 1,value = "登录账号")
	private String account;
	
	@ExcelProperty(index = 2,value = "联系方式")
	private String mobile;
	
	@ExcelProperty(index = 3,value = "账号级别")
	private String levelStr;
	
	@ExcelProperty(index = 4,value = "所属教育局")
	private String officeName;
	
	@ExcelProperty(index = 5,value = "所属学校")
	private String schoolName;
	
	@ExcelProperty(index = 6,value = "角色列表")
	private String roleListStr;
	
	@ExcelProperty(index = 7,value = "创建时间")
	@DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
