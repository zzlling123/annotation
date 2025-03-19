package com.xinkao.erp.common.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.xinkao.erp.common.excel.ExcelCellWriteHandler;
import com.xinkao.erp.common.excel.style.CellStyleStrategy;
import com.xinkao.erp.common.model.BaseExcelModel;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * excel工具类
 */
@Slf4j
public class ExcelUtil {

    private ExcelUtil(){}


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
        Class<T> classType, WriteHandler[] writeHandlers,int relativeHeadRowIndex) throws IOException {
        writeExcel(response, list,  fileName, sheetName, ExcelTypeEnum.XLSX, classType, writeHandlers,relativeHeadRowIndex);
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
     * 导出 Excel ：一个 sheet
     * @param os
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
    public static <T extends BaseExcelModel> void writeExcel(OutputStream os, List<T> list,String sheetName, ExcelTypeEnum excelTypeEnum,
    		Class<T> classType, WriteHandler[] writeHandlers) throws IOException {
    	try {
    		ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(os).head(classType);
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
    		throw new IOException("生成提示文件错误");
    	}
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
        String fileName, String sheetName,Class<T> classType,
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
     * @Title: writeExcel
     * @Description: 动态表头导出数据
     * @param response
     * @param fileName 文件名
     * @param sheetName sheet名
     * @param headList 表头
     * @param dataList 数据
     * @param rowIndex 表头开始行
     * @throws IOException
     * @return: void
     * @date:   2023年4月24日 下午4:17:36
     * @throws
     */
    public static void writeExcel(HttpServletResponse response, String fileName, String sheetName, List<List<String>> headList,
                                  List<List<Object>> dataList, int rowIndex) throws IOException {
        ServletOutputStream outputStream = null;
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码，所有通过后端的文件下载都可以如此处理
            fileName = URLEncoder.encode(fileName, "UTF-8");
            //建议加上该段，否则可能会出现前端无法获取Content-disposition
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            outputStream = response.getOutputStream();
            rowIndex = rowIndex <= 0 ? 1 : rowIndex - 1;
            EasyExcel.write(outputStream)
                    .head(headList)
                    .relativeHeadRowIndex(rowIndex)
                    .sheet(StringUtils.isNotBlank(sheetName) ? sheetName : "sheet1")
                    .doWrite(dataList);
        } catch (Exception e) {
            log.error("导出excel出错：{}", e.getMessage());
            // 重置response
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.reset();
            throw new IOException();
        } finally {
            outputStream.close();
        }
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
                                                             Class<T> classType, WriteHandler[] writeHandlers,int relativeHeadRowIndex) throws IOException {
        writeExcel(response, list,  fileName, sheetName, excelTypeEnum, classType, null, writeHandlers,relativeHeadRowIndex);
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
                                                             Class<T> classType, Set<String> excludeColumnFiledNames, WriteHandler[] writeHandlers,int relativeHeadRowIndex) throws IOException {
        try {
            ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(getOutputStream(fileName, response, excelTypeEnum)).head(classType).relativeHeadRowIndex(relativeHeadRowIndex);
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
     * 导出 Excel ：一个 sheet
     * @param response
     * @param dataList
     * @param fileName
     * @param sheetName
     * @param excelTypeEnum
     * @param classType
     * @param excludeColumnFiledNames
     * @param writeHandlers
     * @throws IOException
     */
    public static void writeExcel(HttpServletResponse response, List<List<String>> dataList,List<String> headList,
            String fileName, String sheetName) throws IOException {
        //转换内容数据
        List<List<String>> headDataList = new ArrayList<List<String>>();
        for (String head : headList) {
            headDataList.add(Arrays.asList(head));
        }
        try {
            ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(getOutputStream(fileName, response, ExcelTypeEnum.XLSX))
                    //设置默认样式及写入头信息开始的行数
                    .useDefaultStyle(true).relativeHeadRowIndex(0)
                    .registerWriteHandler(new CellStyleStrategy(new WriteCellStyle(), new WriteCellStyle()))
                    // 统一列宽,如需设置自动列宽则new LongestMatchColumnWidthStyleStrategy()
                    .registerWriteHandler(new SimpleColumnWidthStyleStrategy(25))
                    .registerWriteHandler(new SimpleRowHeightStyleStrategy((short) 30, (short) 15))
                    .head(headDataList);
            if(null == dataList){
                dataList = new ArrayList<>();
            }
            // 表头、内容样式设置
            excelWriterBuilder.sheet(sheetName).doWrite(dataList);
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
     * 导出 Excel ：一个 sheet
     * @param response
     * @param dataList
     * @param fileName
     * @param sheetName
     * @param excelTypeEnum
     * @param classType
     * @param excludeColumnFiledNames
     * @param writeHandlers
     * @throws IOException
     */
    public static void writeExcel(HttpServletResponse response,String template, List<?> dataList,String fileName, String sheetName) throws IOException {
    	try {
    		ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(getOutputStream(fileName, response, ExcelTypeEnum.XLSX))
    				//设置默认样式及写入头信息开始的行数
    				.useDefaultStyle(true).relativeHeadRowIndex(1)
    				.registerWriteHandler(new SimpleRowHeightStyleStrategy((short) 30, (short) 15));
    		if(null == dataList){
    			dataList = new ArrayList<>();
    		}
    		FillConfig build = FillConfig.builder()
    									.autoStyle(true)
    									.forceNewRow(false)
    									.direction(WriteDirectionEnum.VERTICAL)
    									.hasInit(false)
    									.build();
    		// 表头、内容样式设置
    		excelWriterBuilder.withTemplate(template).sheet(sheetName).doFill(dataList, build);
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
    private static OutputStream getOutputStream(String fileName, HttpServletResponse response, ExcelTypeEnum excelTypeEnum) throws IOException{
        fileName= fileName + excelTypeEnum.getValue();
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
