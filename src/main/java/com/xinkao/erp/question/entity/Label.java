package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 题库自定义标签
 * </p>
 *
 * @author Ldy
 * @since 2025-03-22 11:19:40
 */
@Getter
@Setter
@TableName("q_label")
public class Label extends DataEntity {

    /**
     * 标签名称
     */
    @TableField("label_name")
    private String labelName;

    /**
     * 状态:0-正常 1-删除
     */
    @TableField("is_del")
    private Integer isDel;


}
