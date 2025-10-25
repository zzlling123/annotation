package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("q_symbol")
public class Symbol extends DataEntity {

    @TableField("symbol_name")
    private String symbolName;

    @TableField("is_del")
    private Integer isDel;


}
