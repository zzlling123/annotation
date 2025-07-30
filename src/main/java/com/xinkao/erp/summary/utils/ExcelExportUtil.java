package com.xinkao.erp.summary.utils;

import com.alibaba.excel.EasyExcel;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

public class ExcelExportUtil {
    public static <T> void writeExcel(List<T> data, Class<T> head, HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
        EasyExcel.write(response.getOutputStream(), head).sheet("成绩").doWrite(data);
    }
}