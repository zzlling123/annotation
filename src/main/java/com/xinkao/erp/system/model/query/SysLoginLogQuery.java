package com.xinkao.erp.system.model.query;

import com.xinkao.erp.common.validation.constraint.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * 日志查询参数
 **/
@Setter
@Getter
public class SysLoginLogQuery {

    /**
     * 开始日期
     */
    @Date(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTs;

    /**
     * 结束日期
     */
    @Date(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTs;

    /**
     * 用户名
     */
    private String username;

}
