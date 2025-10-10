package com.xinkao.erp.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.system.entity.GlobleSeq;


@Repository
public interface TableOptMapper extends BaseMapper<GlobleSeq> {

	
	List<String> existTable(@Param("query")String tableName);
	
	
	void createTable(String newTable,String originTable);
	
	List<String> queryList(@Param("query")String tableName);
}
