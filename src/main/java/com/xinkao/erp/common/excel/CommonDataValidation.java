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

public class CommonDataValidation {


    public CommonDataValidation() {

    }

    private String getHiddenSheetName(Sheet sheet, int colIndex, List<String> stringList) {
        Workbook workbook = sheet.getWorkbook();
        String hiddenSheetName = "categoryHidden" + colIndex;
        Sheet hiddenSheet = workbook.createSheet(hiddenSheetName);
        for (int i = 0; i < stringList.size(); i++) {
            hiddenSheet.createRow(i).createCell(0).setCellValue(stringList.get(i));
        }
        Name categoryName = workbook.createName();
        categoryName.setNameName(hiddenSheetName);
        categoryName.setRefersToFormula(hiddenSheetName + "!$A$1:$A$" + (stringList.size()));
        workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheetName), true);
        return hiddenSheetName;
    }

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


    public void setBaseCommonDataValidation(Sheet sheet, int validationType, int operatorType,
                                                      int rowIndex, int colIndex, String vMin, String vMax, String errorTitle, String errorMsg) {

        if(null==sheet || rowIndex<0 || colIndex <0) {
            return;
        }
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
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        setDataValidationErrorMessage(validation, errorTitle, errorMsg);
        sheet.addValidationData(validation);
    }

    private void setDataValidationErrorMessage(DataValidation validation, String errorTitle, String errorMsg) {
        validation.createErrorBox(errorTitle, errorMsg);
    }

    private int getLastRowIndex(Sheet sheet) {
        return 65535;
    }

}
