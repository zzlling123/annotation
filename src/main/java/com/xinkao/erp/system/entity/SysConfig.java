package com.xinkao.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 系统配置-系统公用
 * </p>
 *
 * @author hanhys
 * @since 2022-05-30 16:11:50
 */
@Getter
@Setter
@TableName("globle_sys_config")
public class SysConfig extends DataEntity {

    /**参数名称**/
    @TableField("config_name")
    private String configName;

    /**参数键名**/
    @TableField("config_key")
    private String configKey;

    /**参数键值**/
    @TableField("config_value")
    private String configValue;

    /**系统内置（1:是，0：否）**/
    @TableField("is_system")
    private Integer isSystem;

}
