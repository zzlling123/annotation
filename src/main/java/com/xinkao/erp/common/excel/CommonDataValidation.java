package com.xinkao.erp.common.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;

import com.xinkao.erp.common.enums.CommonEnum;

/**
 *通用单元格验证规则
 */
public class CommonDataValidation {


    public CommonDataValidation() {

    }

    /**
     * 防止数据过多直接设置下拉选项导致无法打开excel，使用隐藏sheet来实现下拉
     * @param sheet
     * @param colIndex
     * @param stringList
     * @return
     */
    private String getHiddenSheetName(Sheet sheet, int colIndex, List<String> stringList) {
        // 创建隐藏域，防止出现下拉数据过多导致excel打不开
        Workbook workbook = sheet.getWorkbook();
        String hiddenSheetName = "categoryHidden" + colIndex;
        Sheet hiddenSheet = workbook.createSheet(hiddenSheetName);
        for (int i = 0; i < stringList.size(); i++) {
            // 循环赋值
            hiddenSheet.createRow(i).createCell(0).setCellValue(stringList.get(i));
        }
        // 创建名称，可被其他单元格引用
        Name categoryName = workbook.createName();
        categoryName.setNameName(hiddenSheetName);
        // 设置名称引用的公式
        // 使用像'A1：B1'这样的相对值会导致在Microsoft Excel中使用工作簿时名称所指向的单元格的意外移动，
        // 通常使用绝对引用，例如'$A$1:$B$1'可以避免这种情况。
        categoryName.setRefersToFormula(hiddenSheetName + "!$A$1:$A$" + (stringList.size()));
        workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheetName), true);
        return hiddenSheetName;
    }

    /**
     * 封装是否类型数据下拉值
     * @param sheet
     * @param rowIndex
     * @param colIndex
     * @return
     */
    public void setYNDataValidation(Sheet sheet, int rowIndex, Integer colIndex) {
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(rowIndex, getLastRowIndex(sheet), colIndex, colIndex);
        DataValidationHelper helper = sheet.getDataValidationHelper();
        List<String> stringList = new ArrayList<>();
        for (CommonEnum.GLOBAL_YN global_yn: CommonEnum.GLOBAL_YN.values()) {
            stringList.add(global_yn.getName());
        }
        DataValidationConstraint constraint = helper.createExplicitListConstraint(stringList.toArray(new String[stringList.size()]));
        DataValidation validation = helper.createValidation(constraint, cellRangeAddressList);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }

    /**
     * 封装枚举类型数据下拉值
     * @param sheet
     * @param rowIndex
     * @param colIndex
     * @param typeCode
     * @return
     */
    public void setDictDataValidation(Sheet sheet, int rowIndex, Integer colIndex,  List<String> dictList) {
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(rowIndex, getLastRowIndex(sheet), colIndex, colIndex);
        DataValidationHelper helper = sheet.getDataValidationHelper();
        String hiddenSheetName = getHiddenSheetName(sheet, colIndex, dictList);
        DataValidationConstraint constraint = helper.createFormulaListConstraint(hiddenSheetName);
        DataValidation validation = helper.createValidation(constraint, cellRangeAddressList);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }
    
	/**
	 * 封装枚举类型数据下拉值
	 * @param sheet
	 * @param rowIndex
	 * @param colIndex
	 * @param stringList
	 * @return
	 */
	public void setStringListDataValidation(Sheet sheet, int rowIndex, Integer colIndex, List<String> stringList) {
		CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(rowIndex, getLastRowIndex(sheet), colIndex, colIndex);
		DataValidationHelper helper = sheet.getDataValidationHelper();
		String hiddenSheetName = getHiddenSheetName(sheet, colIndex, stringList);
		DataValidationConstraint constraint = helper.createFormulaListConstraint(hiddenSheetName);
		DataValidation validation = helper.createValidation(constraint, cellRangeAddressList);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
		sheet.addValidationData(validation);
	}


    /**
     * 设置通用的校验规则
     * @param sheet
     * @param validationType
     * @param operatorType
     * @param rowIndex
     * @param colIndex
     * @param vMin
     * @param vMax
     * @param errorTitle
     * @param errorMsg
     */
    public void setBaseCommonDataValidation(Sheet sheet, int validationType, int operatorType,
                                                      int rowIndex, int colIndex, String vMin, String vMax, String errorTitle, String errorMsg) {

        if(null==sheet || rowIndex<0 || colIndex <0) {
            return;
        }
        //两个值都为空则返回
        vMin = null == vMin ? "" : vMin.trim();
        vMax = null == vMax ? "" : vMax.trim();
        if("".equals(vMin) && "".equals(vMax)) {
            return;
        }
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = null;
        if(validationType == DataValidationConstraint.ValidationType.TEXT_LENGTH) {
            dvConstraint = helper.createTextLengthConstraint(operatorType, vMin, vMax);
        } else if(validationType == DataValidationConstraint.ValidationType.DECIMAL) {
            dvConstraint = helper.createDecimalConstraint(operatorType, vMin, vMax);
        } else if(validationType == DataValidationConstraint.ValidationType.INTEGER) {
            dvConstraint = helper.createIntegerConstraint(operatorType, vMin, vMax);
        } else if(validationType == DataValidationConstraint.ValidationType.DATE) {
            dvConstraint = helper.createDateConstraint(operatorType, vMin, vMax, null);
        } else if(validationType == DataValidationConstraint.ValidationType.TIME) {
            dvConstraint = helper.createTimeConstraint(operatorType, vMin, vMax);
        }
        if(null == dvConstraint) {
            return;
        }
        CellRangeAddressList addressList = new CellRangeAddressList(rowIndex, getLastRowIndex(sheet), colIndex, colIndex);
        DataValidation validation = helper.createValidation(dvConstraint, addressList);
        //设置出错提示信息
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        setDataValidationErrorMessage(validation, errorTitle, errorMsg);
        sheet.addValidationData(validation);
    }

    /**
     * 设置校验不通过信息提示
     * @param validation
     * @param errorTitle
     * @param errorMsg
     */
    private void setDataValidationErrorMessage(DataValidation validation, String errorTitle, String errorMsg) {
        validation.createErrorBox(errorTitle, errorMsg);
    }

    /**
     * 获取excel最大行数
     * @param sheet
     * @return
     */
    private int getLastRowIndex(Sheet sheet) {
        return 65535;
    }

}
