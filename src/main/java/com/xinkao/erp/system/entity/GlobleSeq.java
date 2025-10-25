package com.xinkao.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("globle_seq")
public class GlobleSeq extends BaseEntity {

    
    @TableId("code")
    private String code;

    
    @TableField("name")
    private String name;

    
    @TableField("increment")
    private Long increment;

    
    @TableField("current_no")
    private Long currentNo;

    
    @TableField("current_value")
    private String currentValue;

}
