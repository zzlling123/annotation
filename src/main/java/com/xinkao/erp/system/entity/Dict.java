package com.xinkao.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("sys_dict")
public class Dict extends DataEntity {

    
    @ApiModelProperty("分类钮")
    @TableField("dict_type")
    private String dictType;

    
    @ApiModelProperty("字典名称")
    @TableField("dict_label")
    private String dictLabel;

    
    @ApiModelProperty("字典值")
    @TableField("dict_value")
    private String dictValue;

    
    @ApiModelProperty("排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty("状态:0-禁用 1-启用")
    @TableField("state")
    private Integer state;

}
