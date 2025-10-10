package com.xinkao.erp.common.model.vo;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageVO<T> {

    private long page;

    private long pageSize;

    private long total;

    private long totalPage;

    private List<T> content;

    public static <T> PageVO<T> reducePage(Page page, List<T> records){
        PageVO pageVO = new PageVO(page.getCurrent(), page.getSize(),
            page.getTotal(), page.getPages(), records);
        return pageVO;
    }

    public static <T> PageVO<T> reducePage(Page page){
        PageVO pageVO = new PageVO(page.getCurrent(), page.getSize(),
            page.getTotal(), page.getPages(), page.getRecords());
        return pageVO;
    }
    
}
