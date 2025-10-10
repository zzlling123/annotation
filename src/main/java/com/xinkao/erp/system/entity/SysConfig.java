package com.xinkao.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("globle_sys_config")
public class SysConfig extends DataEntity {

    
    @TableField("config_name")
    private String configName;

    
    @TableField("config_key")
    private String configKey;

    
    @TableField("config_value")
    private String configValue;

    
    @TableField("is_system")
    private Integer isSystem;

}
