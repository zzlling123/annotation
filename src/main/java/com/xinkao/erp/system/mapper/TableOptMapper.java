package com.xinkao.erp.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.system.entity.GlobleSeq;

/**
 * <p>
 * 分表通用方法 Mapper 接口
 * </p>
 *
 * @author hanhys
 * @since 2022-05-30 16:11:50
 */
@Repository
public interface TableOptMapper extends BaseMapper<GlobleSeq> {

	/**
	 * 判断表是否存在
	 * @param tableName 表名
	 * @return
	 */
	List<String> existTable(@Param("query")String tableName);
	
	/**
	 * 创建表
	 * @param newTable 新表
	 * @param originTable 参照表
	 * @return
	 */
	void createTable(String newTable,String originTable);
	/**
	 * 查询表列表
	 * @param tableName
	 * @return
	 */
	List<String> queryList(@Param("query")String tableName);
}
