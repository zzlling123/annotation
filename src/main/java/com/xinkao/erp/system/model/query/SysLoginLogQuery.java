package com.xinkao.erp.system.model.query;

import com.xinkao.erp.common.validation.constraint.Date;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class SysLoginLogQuery {

    
    @Date(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTs;

    
    @Date(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTs;

    
    private String username;

}
