package com.xinkao.erp.common.excel.style;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;

public class CellStyleStrategy extends HorizontalCellStyleStrategy {
    private final WriteCellStyle headWriteCellStyle;
    private final WriteCellStyle contentWriteCellStyle;


    public CellStyleStrategy(WriteCellStyle headWriteCellStyle, WriteCellStyle contentWriteCellStyle) {
        this.headWriteCellStyle = headWriteCellStyle;
        this.contentWriteCellStyle = contentWriteCellStyle;
    }

    @Override
    protected void setHeadCellStyle( CellWriteHandlerContext context) {
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontName("宋体");
     
        headWriteCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        headWriteFont.setFontHeightInPoints((short) 11);
        headWriteFont.setBold(false);
        
        headWriteCellStyle.setWriteFont(headWriteFont);
        if (stopProcessing(context)) {
            return;
        }
        WriteCellData<?> cellData = context.getFirstCellData();
        WriteCellStyle.merge(headWriteCellStyle, cellData.getOrCreateStyle());
    }

    @Override
    protected void setContentCellStyle(CellWriteHandlerContext context) {
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontName("宋体");
        contentWriteFont.setFontHeightInPoints((short) 11);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        WriteCellData<?> cellData = context.getFirstCellData();
        WriteCellStyle.merge(contentWriteCellStyle, cellData.getOrCreateStyle());
    }
}
