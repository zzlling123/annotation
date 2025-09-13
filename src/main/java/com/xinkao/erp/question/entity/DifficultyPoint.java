package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Ldy
 * @since 2025-09-13 18:28:39
 */
@Getter
@Setter
@TableName("q_difficulty_point")
public class DifficultyPoint extends DataEntity {

    /**
     * 难度等级
     */
    @TableField("difficulty_level")
    private Integer difficultyLevel;

    /**
     * 难度内容
     */
    @TableField("point_name")
    private String pointName;

    /**
     * 状态:0-正常 1-删除
     */
    @TableField("is_del")
    private Integer isDel;


}
