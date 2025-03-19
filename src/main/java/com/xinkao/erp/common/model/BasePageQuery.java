package com.xinkao.erp.common.model;

import com.xinkao.erp.common.model.support.Pageable;

import lombok.Data;

/**
 * 分页查询基类
 **/
@Data
public class BasePageQuery extends BaseQuery{
    /**
     * 数据权限过滤
     */
	/**分页信息**/
    private Pageable pageInfo;
	
}
