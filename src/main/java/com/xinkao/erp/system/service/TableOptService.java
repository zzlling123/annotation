package com.xinkao.erp.system.service;

import java.util.List;

import com.xinkao.erp.common.enums.system.TableSplitEnum;

/**
 * 表相关的操作
 * @author hys_thanks
 */
public interface TableOptService {

	/**
	 * 判断表名是否存在
	 * @param tableSplit 表常量
	 * @param tableRule 分表规则
	 * @return
	 */
	boolean existTable(TableSplitEnum tableSplit,String tableRule);
	/**
	 * 创建新表
	 * @param tableSplit 表常量
	 * @param tableRule 分表规则
	 * @return
	 */
	boolean createIfNotExist(TableSplitEnum tableSplit,String tableRule);
	/**
	 * 获取新表名
	 * @return
	 */
	String getNewTable(TableSplitEnum tableSplit,String tableRule);
	/**
	 * 获取所有分表列表
	 * @return
	 */
	List<String> getTableList(TableSplitEnum tableSplit);
	/**
	 * 分表规则
	 * @param dateStr
	 * @return
	 */
	String getDateStr(String dateStr);
	/**
	 * 分表规则
	 * @param dateStr
	 * @return
	 */
	String getDateYearStr(String dateStr);
}
