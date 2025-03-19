package com.xinkao.erp.common.model.vo;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 返回给前端时的分页对象
 **/
@Data
@AllArgsConstructor
public class PageVO<T> {

    /**当前页**/
    private long page;

    /**每页条数**/
    private long pageSize;

    /**总条数**/
    private long total;

    /**总页数**/
    private long totalPage;

    /**内容**/
    private List<T> content;

    /**
     * 简化返回的page内容，并指定结果集
     * @param page
     * @param records
     * @return
     */
    public static <T> PageVO<T> reducePage(Page page, List<T> records){
        //jpa的页数从0开始，系统改为从1开始
        PageVO pageVO = new PageVO(page.getCurrent(), page.getSize(),
            page.getTotal(), page.getPages(), records);
        return pageVO;
    }

    public static <T> PageVO<T> reducePage(Page page){
        //jpa的页数从0开始，系统改为从1开始
        PageVO pageVO = new PageVO(page.getCurrent(), page.getSize(),
            page.getTotal(), page.getPages(), page.getRecords());
        return pageVO;
    }
    
//    public static <T> PageVO<T> listToPage(Page page, List<T> list) {
//    	int currentPage = Long.valueOf(page.getCurrent()).intValue();
//    	int pageSize = Long.valueOf(page.getSize()).intValue();
//        int listSize = list.size();
//        int totalPage = (listSize + pageSize -1) / pageSize;
//        int startIndex = Math.min((currentPage - 1) * pageSize, listSize);
//        int endIndex = Math.min(startIndex + pageSize, listSize);
//        List<T> records = list.subList(startIndex, endIndex);
// 
//        PageVO pageVO = new PageVO(page.getCurrent(), page.getSize(),
//                Long.valueOf(listSize), totalPage, records);
//        return pageVO;
//    }
}
