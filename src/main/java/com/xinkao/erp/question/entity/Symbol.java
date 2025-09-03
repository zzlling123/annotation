package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 题目标记名称表
 * </p>
 *
 * @author Ldy
 * @since 2025-09-03 19:13:25
 */
@Getter
@Setter
@TableName("q_symbol")
public class Symbol extends DataEntity {

    /**
     * 题目来源名称
     */
    @TableField("symbol_name")
    private String symbolName;

    /**
     * 状态:0-正常 1-删除
     */
    @TableField("is_del")
    private Integer isDel;


}
