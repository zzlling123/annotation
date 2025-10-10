package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("q_label")
public class Label extends DataEntity {

    @TableField("label_name")
    private String labelName;

    @TableField("is_del")
    private Integer isDel;


}
