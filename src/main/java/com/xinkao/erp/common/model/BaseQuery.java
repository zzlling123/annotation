package com.xinkao.erp.common.model;

import lombok.Data;
/**
 * 查询基类
 **/
@Data
public class BaseQuery {
    /**
     * 用户主键
     */
    private String innerUserId;
    /**
     * 所属等级
     */
    private int innerLevel;
    /**
     * 所属机构主键
     */
    private String innerOfficeId;
    /**
     * 所属学校主键
     */
    private String innerSchoolId;
}
