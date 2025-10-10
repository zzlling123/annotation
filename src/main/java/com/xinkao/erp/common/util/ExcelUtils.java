package com.xinkao.erp.common.util;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.xinkao.erp.common.excel.ExcelCellWriteHandler;
import com.xinkao.erp.common.model.BaseExcelModel;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Slf4j
public class ExcelUtils {

    private ExcelUtils(){}


    
    public static <T extends BaseExcelModel>  void writeExcel(HttpServletResponse response, List<T> list,
                                                              Class<T> classType) throws IOException {
        String defaultFileName = DateUtil.current() + "";
        String defaultSheetName = "sheet";
        writeExcel(response, list,  defaultFileName, defaultSheetName, ExcelTypeEnum.XLSX, classType, null);
    }

    
    public static <T extends BaseExcelModel>  void writeExcel(HttpServletResponse response, List<T> list,
        Class<T> classType, Set<String> excludeColumnFiledNames) throws IOException {
        String defaultFileName = DateUtil.current() + "";
        String defaultSheetName = "sheet";
        writeExcel(response, list,  defaultFileName, defaultSheetName, ExcelTypeEnum.XLSX, classType, excludeColumnFiledNames, null);
    }


    
    public static <T extends BaseExcelModel>  void writeExcel(HttpServletResponse response, List<T> list,
        String fileName, String sheetName,
        Class<T> classType) throws IOException {
        writeExcel(response, list,  fileName, sheetName, ExcelTypeEnum.XLSX, classType,null);
    }

    
    public static <T extends BaseExcelModel>  void writeExcel(HttpServletResponse response, List<T> list,
        String fileName, String sheetName,
        Class<T> classType, WriteHandler[] writeHandlers) throws IOException {
        writeExcel(response, list,  fileName, sheetName, ExcelTypeEnum.XLSX, classType, writeHandlers);
    }

    
    public static <T extends BaseExcelModel>  void writeExcel(HttpServletResponse response, List<T> list,
        String fileName, String sheetName,
        Class<T> classType, Set<String> excludeColumnFiledNames) throws IOException {
        writeExcel(response, list,  fileName, sheetName, ExcelTypeEnum.XLSX, classType, excludeColumnFiledNames,null);
    }

    
    public static <T extends BaseExcelModel> void writeExcel(HttpServletResponse response, List<T> list,
        String fileName, String sheetName, Class<T> classType,
        Set<String> excludeColumnFiledNames, WriteHandler[] writeHandlers) throws IOException {
        writeExcel(response, list, fileName, sheetName, ExcelTypeEnum.XLSX, classType, excludeColumnFiledNames, writeHandlers);
    }

    
    public static <T extends BaseExcelModel> void writeExcel(HttpServletResponse response, List<T> list,
        String fileName, String sheetName, ExcelTypeEnum excelTypeEnum,
        Class<T> classType, WriteHandler[] writeHandlers) throws IOException {
        writeExcel(response, list,  fileName, sheetName, excelTypeEnum, classType, null, writeHandlers);
    }

    
    public static <T extends BaseExcelModel> void writeExcel(HttpServletResponse response, List<T> list,
        String fileName, String sheetName, ExcelTypeEnum excelTypeEnum,
        Class<T> classType, Set<String> excludeColumnFiledNames, WriteHandler[] writeHandlers) throws IOException {
        try {
            ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(getOutputStream(fileName, response, excelTypeEnum)).head(classType);
            if(null != excludeColumnFiledNames && excludeColumnFiledNames.size() > 0){
                excelWriterBuilder.excludeColumnFiledNames(excludeColumnFiledNames);
            }

            if (null != writeHandlers) {
                for (WriteHandler writeHandler: writeHandlers) {
                    if(writeHandler instanceof ExcelCellWriteHandler){
                        excelWriterBuilder.inMemory(Boolean.TRUE);
                    }
                    excelWriterBuilder.registerWriteHandler(writeHandler);
                }
            }

            if(null == list){
                list = new ArrayList<>();
            }
            excelWriterBuilder.sheet(sheetName).doWrite(list);
        } catch (Exception e) {
            log.error("导出excel出错：{}", e.getMessage());
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            throw new IOException();
        }
    }

    
    public static OutputStream getOutputStream(
			String fileName, HttpServletResponse response, ExcelTypeEnum excelTypeEnum) throws IOException {
		if (null != excelTypeEnum) {
			fileName = fileName + excelTypeEnum.getValue();
		}
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            fileName = URLEncoder.encode(fileName, "UTF-8");
            //            fileName = new String(fileName.getBytes(), "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            return response.getOutputStream();
        } catch (IOException e) {
            log.error("创建文件失败！");
            throw new IOException("创建文件失败！");
        }
    }
}
