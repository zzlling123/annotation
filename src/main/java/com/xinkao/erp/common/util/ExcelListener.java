package com.xinkao.erp.common.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelListener<T> extends AnalysisEventListener<T> {

    private List<T> datas = new ArrayList<>();


    
    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        Integer currentRowNum = analysisContext.getCurrentRowNum();
//        if(currentRowNum >= 2){
//            datas.add(t);
//        }
        datas.add(t);
    }

    
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}