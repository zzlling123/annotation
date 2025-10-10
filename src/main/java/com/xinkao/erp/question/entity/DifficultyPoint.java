package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("q_difficulty_point")
public class DifficultyPoint extends DataEntity {

    @TableField("difficulty_level")
    private Integer difficultyLevel;

    @TableField("point_name")
    private String pointName;

    @TableField("is_del")
    private Integer isDel;


}
