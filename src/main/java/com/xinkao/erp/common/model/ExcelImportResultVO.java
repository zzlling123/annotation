package com.xinkao.erp.common.model;

import java.util.List;
import java.util.Map;

/**
 * 文件导入后返回json数据
 **/
public class ExcelImportResultVO<T> {

    private String fileName;

    private Integer totalSize = 0;

    private Integer errorSize = 0;

    private List<T> dataList;

    private List<Map<Integer, String>> errorList;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }

    public Integer getErrorSize() {
        return errorSize;
    }

    public void setErrorSize(Integer errorSize) {
        this.errorSize = errorSize;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public List<Map<Integer, String>> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<Map<Integer, String>> errorList) {
        this.errorList = errorList;
    }
}
