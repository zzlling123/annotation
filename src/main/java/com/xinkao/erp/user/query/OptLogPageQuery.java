package com.xinkao.erp.user.query;

import com.xinkao.erp.common.model.BasePageQuery;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class OptLogPageQuery extends BasePageQuery {

    
    private String startTime;

    
    private String endTime;

    
    private String realName;

    
    private String account;

}