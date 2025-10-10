package com.xinkao.erp.common.excel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;

public class FreezeSheetWriteHandler implements SheetWriteHandler {

    private int colSplit;

    private int rowSplit;

    public FreezeSheetWriteHandler(int colSplit, int rowSplit) {
        this.colSplit = colSplit;
        this.rowSplit = rowSplit;
    }

    public FreezeSheetWriteHandler() {
        this(0, 1);
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder,
        WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder,
        WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        sheet.createFreezePane(colSplit, rowSplit);
    }
}
