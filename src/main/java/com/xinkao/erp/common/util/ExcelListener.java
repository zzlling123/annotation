package com.xinkao.erp.common.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelListener<T> extends AnalysisEventListener<T> {

    //自定义用于暂时存储data
    private List<T> datas = new ArrayList<>();


    /**
     * 每一条数据解析都会来调用
     */
    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        Integer currentRowNum = analysisContext.getCurrentRowNum();
//        if(currentRowNum >= 2){
//            datas.add(t);
//        }
        datas.add(t);
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}