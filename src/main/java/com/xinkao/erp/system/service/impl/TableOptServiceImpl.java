package com.xinkao.erp.system.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xinkao.erp.common.enums.system.TableSplitEnum;
import com.xinkao.erp.system.mapper.TableOptMapper;
import com.xinkao.erp.system.service.TableOptService;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 表操作相关的服务
 * @author hys_thanks
 */
@Slf4j
@Service
public class TableOptServiceImpl implements TableOptService {
	
	@Resource
	private TableOptMapper optMapper;
	
	@Override
	public boolean existTable(TableSplitEnum tableSplit, String tableRule) {
		String newTable = getNewTable(tableSplit, tableRule);
		List<String> existTable = optMapper.existTable(newTable);
		boolean result = !existTable.isEmpty();
		log.debug("正在查询[{},{}]是否存在:[{}]",tableSplit.getDesc(),newTable,result);
		return result;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean createIfNotExist(TableSplitEnum tableSplit, String tableRule) {
		String newTable = getNewTable(tableSplit, tableRule);
		log.debug("正在创建[{}]分表:[{}]",tableSplit.getDesc(),newTable);
		optMapper.createTable(newTable, tableSplit.getTableName());
		return true;
	}

	@Override
	public String getNewTable(TableSplitEnum tableSplit, String tableRule) {
		return tableSplit.getTableName()+"_"+tableRule;
	}

	@Override
	public List<String> getTableList(TableSplitEnum tableSplit) {
		return optMapper.queryList(tableSplit.getTableName());
	}

	@Override
	public String getDateStr(String dateStr) {
		if(StringUtils.isBlank(dateStr)) {
			dateStr = DateUtil.date().toString("yyyyMM");
		}
		return StringUtils.trim(dateStr);
	}

	@Override
	public String getDateYearStr(String dateStr) {
		if(StringUtils.isBlank(dateStr)) {
			dateStr = DateUtil.date().toString("yyyy");
		}
		return StringUtils.trim(dateStr);
	}

}
