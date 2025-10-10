package com.xinkao.erp.common.model;

import com.xinkao.erp.common.model.support.Pageable;

import lombok.Data;

@Data
public class BasePageQuery extends BaseQuery{
    private Pageable pageInfo;
	
}
