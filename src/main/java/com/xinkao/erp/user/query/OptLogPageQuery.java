package com.xinkao.erp.user.query;

import com.xinkao.erp.common.model.BasePageQuery;

import lombok.Getter;
import lombok.Setter;

/**
 * 操作日志查询
 * @author hys_thanks
 */
@Setter
@Getter
public class OptLogPageQuery extends BasePageQuery {

    /**日志查询-开始日期**/
    private String startTime;

    /**日志查询-结束日期**/
    private String endTime;

    /**用户姓名**/
    private String realName;

    /**账户**/
    private String account;

}