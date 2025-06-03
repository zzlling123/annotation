package com.xinkao.erp.summary.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

@TableName("q_question_shape")
@Setter
@Getter
public class Shape extends DataEntity {

    @TableField("shape_code")
    private String shapeCode;
    @TableField("shape_name")
    private String shapeName;
}
