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

/**
 * Util - excel工具类
 * @Description:
 * @Author: sk
 * @Date: 2019/12/03 14:08
 */
@Slf4j
public class ExcelUtils {

    private ExcelUtils(){}


    /**
     * 默认导出的Excel
     * @param response HttpServletResponse
     * @param list 数据 list，于Excel模型对应
     * @param classType Excel模型Class
     * @throws IOException
     */
    public static <T extends BaseExcelModel>  void writeExcel(HttpServletResponse response, List<T> list,
                                                              Class<T> classType) throws IOException {
        String defaultFileName = DateUtil.current() + "";
        String defaultSheetName = "sheet";
        writeExcel(response, list,  defaultFileName, defaultSheetName, ExcelTypeEnum.XLSX, classType, null);
    }

    /**
     * @param response
     * @param list
     * @param classType
     * @param excludeColumnFiledNames  排除的列
     * @param <T>
     * @throws IOException
     */
    public static <T extends BaseExcelModel>  void writeExcel(HttpServletResponse response, List<T> list,
        Class<T> classType, Set<String> excludeColumnFiledNames) throws IOException {
        String defaultFileName = DateUtil.current() + "";
        String defaultSheetName = "sheet";
        writeExcel(response, list,  defaultFileName, defaultSheetName, ExcelTypeEnum.XLSX, classType, excludeColumnFiledNames, null);
    }


    /**
     * 导出 Excel ：一个 sheet
     * 默认按照xlsx格式导出
     * @param response HttpServletResponse
     * @param list 数据 list，于excelModel对应
     * @param fileName 导出的文件名
     * @param sheetName 导出的 sheet 名
     * @param classType Excel模型Class
     * @throws IOException
     */
    public static <T extends BaseExcelModel>  void writeExcel(HttpServletResponse response, List<T> list,
        String fileName, String sheetName,
        Class<T> classType) throws IOException {
        writeExcel(response, list,  fileName, sheetName, ExcelTypeEnum.XLSX, classType,null);
    }

    /**
     * 导出 Excel ：一个 sheet
     * 默认按照xlsx格式导出
     * @param response HttpServletResponse
     * @param list 数据 list，于excelModel对应
     * @param fileName 导出的文件名
     * @param sheetName 导出的 sheet 名
     * @param classType Excel模型Class
     * @param  writeHandlers 自定义拦截器
     * @throws IOException
     */
    public static <T extends BaseExcelModel>  void writeExcel(HttpServletResponse response, List<T> list,
        String fileName, String sheetName,
        Class<T> classType, WriteHandler[] writeHandlers) throws IOException {
        writeExcel(response, list,  fileName, sheetName, ExcelTypeEnum.XLSX, classType, writeHandlers);
    }

    /**
     *
     * @param response
     * @param list
     * @param fileName
     * @param sheetName
     * @param classType
     * @param excludeColumnFiledNames   排除的列
     * @param <T>
     * @throws IOException
     */
    public static <T extends BaseExcelModel>  void writeExcel(HttpServletResponse response, List<T> list,
        String fileName, String sheetName,
        Class<T> classType, Set<String> excludeColumnFiledNames) throws IOException {
        writeExcel(response, list,  fileName, sheetName, ExcelTypeEnum.XLSX, classType, excludeColumnFiledNames,null);
    }

    /**
     * 导出 Excel ：一个 sheet
     *
     * @param response      HttpServletResponse
     * @param list          数据 list，于excelModel对应
     * @param fileName      导出的文件名
     * @param sheetName     导出的 sheet 名
     * @param classType     Excel模型Class
     * @param excludeColumnFiledNames  排除的列
     * @param writeHandlers 自定义拦截器
     * @throws IOException
     */
    public static <T extends BaseExcelModel> void writeExcel(HttpServletResponse response, List<T> list,
        String fileName, String sheetName, Class<T> classType,
        Set<String> excludeColumnFiledNames, WriteHandler[] writeHandlers) throws IOException {
        writeExcel(response, list, fileName, sheetName, ExcelTypeEnum.XLSX, classType, excludeColumnFiledNames, writeHandlers);
    }

    /**
     * 导出 Excel ：一个 sheet
     *
     * @param response      HttpServletResponse
     * @param list          数据 list，于excelModel对应
     * @param fileName      导出的文件名
     * @param sheetName     导出的 sheet 名
     * @param excelTypeEnum Excel导出类型
     * @param classType     Excel模型Class
     * @param writeHandlers 自定义拦截器
     * @throws IOException
     */
    public static <T extends BaseExcelModel> void writeExcel(HttpServletResponse response, List<T> list,
        String fileName, String sheetName, ExcelTypeEnum excelTypeEnum,
        Class<T> classType, WriteHandler[] writeHandlers) throws IOException {
        writeExcel(response, list,  fileName, sheetName, excelTypeEnum, classType, null, writeHandlers);
    }

    /**
     * 导出 Excel ：一个 sheet
     * @param response
     * @param list
     * @param fileName
     * @param sheetName
     * @param excelTypeEnum
     * @param classType
     * @param excludeColumnFiledNames
     * @param writeHandlers
     * @param <T>
     * @throws IOException
     */
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

    /**
     * 导出文件时为Writer生成OutputStream
     * @param fileName 导出的文件名
     * @param response HttpServletResponse
     * @param excelTypeEnum Excel导出的类型
     * @return
     * @throws IOException
     */
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
