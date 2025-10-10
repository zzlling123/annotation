package com.xinkao.erp.system.service;

import java.util.List;

import com.xinkao.erp.common.enums.system.TableSplitEnum;


public interface TableOptService {

	
	boolean existTable(TableSplitEnum tableSplit,String tableRule);
	
	boolean createIfNotExist(TableSplitEnum tableSplit,String tableRule);
	
	String getNewTable(TableSplitEnum tableSplit,String tableRule);
	
	List<String> getTableList(TableSplitEnum tableSplit);
	
	String getDateStr(String dateStr);
	
	String getDateYearStr(String dateStr);
}
