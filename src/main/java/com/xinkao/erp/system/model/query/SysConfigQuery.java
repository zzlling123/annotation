package com.xinkao.erp.system.model.query;

import lombok.Getter;
import lombok.Setter;

/**
 *参数配置查询类
 **/
@Setter
@Getter
public class SysConfigQuery {

    /**根据参数名称模糊查询**/
    private String configName;

    /**根据参数键值模糊查询**/
    private String configKey;

    /**是否系统内置，1：是, 0：否**/
    private Integer system;
}
