package com.xinkao.erp.common.excel;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.xinkao.erp.common.annotation.Excel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

@Slf4j
public class ExcelCellWriteHandler<T> implements CellWriteHandler {

	public Class<T> clazz;
	private List<Object[]> fields;

	private CommonDataValidation commonDataValidation;

	public ExcelCellWriteHandler(Class<T> clazz) {
		this.clazz = clazz;
		commonDataValidation = new CommonDataValidation();
		List<Field> tempFields = new ArrayList<>();
		tempFields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
		tempFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		this.fields = new ArrayList<>();
		for (Field field : tempFields) {
			if (field.isAnnotationPresent(Excel.class)) {
				putToField(field, field.getAnnotation(Excel.class));
			}
		}
	}

	private void putToField(Field field, Excel attr) {
		if (attr != null) {
			this.fields.add(new Object[] { field, attr });
		}
	}

	@Override
	public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row,
			Head head, Integer integer, Integer integer1, Boolean isHead) {
	}

	@Override
	public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell,
			Head head, Integer integer, Boolean isHead) {
		Sheet sheet = writeSheetHolder.getSheet();
		if (isHead) {
			String fieldName = head.getFieldName();
			for (Object[] os : fields) {
				 Field field = (Field) os[0];
	                Excel excel = (Excel) os[1];
	                if (fieldName.equals(field.getName())) {
	                    if (StrUtil.isNotBlank(excel.prompt())) {
	                        setPrompt(sheet, cell, excel.prompt());
	                    }
	                }
			}
		}
	}

	@Override
	public void afterCellDataConverted(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
			WriteCellData<?> cellData, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
		Sheet sheet = writeSheetHolder.getSheet();
		int cellIndex = cell.getColumnIndex();
		if (!isHead) {
			String fieldName = head.getFieldName();
			for (Object[] os : fields) {
				Field field = (Field) os[0];
				Excel excel = (Excel) os[1];
				if (fieldName.equals(field.getName())) {
                    String readConverterExp = excel.readConverterExp();
                    String separator = excel.separator();
                    String[] dict = excel.dict();
                    if (StrUtil.isNotBlank(readConverterExp)) {
                       String cellValue = convertByExp(Convert.toStr(cellData.getStringValue()), readConverterExp,
                            separator);
                        cellData.setStringValue(cellValue);
                    }
                    if (dict != null && dict.length > 0) {
                    	List<String> dictList = Stream.of(dict).collect(Collectors.toList());
                    	 commonDataValidation.setDictDataValidation(sheet, 1, cellIndex, dictList);
                    }
                }
			}
		}
	}

	private void setPrompt(Sheet sheet, Cell cell, String prompt) {
		Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
		Comment comment = drawingPatriarch
				.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 2, 2, (short) 3, 6));
		comment.setString(new XSSFRichTextString(prompt));
		cell.setCellComment(comment);
	}

	private String convertByExp(String propertyValue, String converterExp, String separator) {
		StringBuilder propertyString = new StringBuilder();
		String[] convertSource = converterExp.split(",");
		for (String item : convertSource) {
			String[] itemArray = item.split("=");
			if (StrUtil.containsAny(propertyValue, separator)) {
				for (String value : propertyValue.split(separator)) {
					if (itemArray[0].equals(value)) {
						propertyString.append(itemArray[1] + separator);
						break;
					}
				}
			} else {
				if (itemArray[0].equals(propertyValue)) {
					return itemArray[1];
				}
			}
		}
		return StrUtil.strip(propertyString.toString(), null, separator);

	}
}
