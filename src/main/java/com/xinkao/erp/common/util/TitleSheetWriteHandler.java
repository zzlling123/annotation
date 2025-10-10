package com.xinkao.erp.common.util;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class TitleSheetWriteHandler implements SheetWriteHandler {
    private String title;
    private int lastCol;
    public TitleSheetWriteHandler(String title,int lastCol){
        this.title = title;
        this.lastCol = lastCol;
    }
    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        Sheet sheet = workbook.getSheetAt(0);

        Row row = sheet.createRow(0);
        row.setHeight((short) 4700);
        Cell cell = row.createCell(0);
        cell.setCellValue(title);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        Font font = workbook.createFont();

        font.setColor(IndexedColors.RED.getIndex());

        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cell.setCellStyle(cellStyle);
        sheet.addMergedRegionUnsafe(new CellRangeAddress(0, 0, 0, lastCol));
    }
}
