package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.BaseEntity;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 题目-标记关联表
 * </p>
 *
 * @author Ldy
 * @since 2025-04-20 22:26:27
 */
@Getter
@Setter
@TableName("q_question_mark")
public class QuestionMark extends BaseEntity {

    /**
     * 题目ID
     */
    @TableField("qid")
    private Integer qid;

    /**
     * 标记ID
     */
    @TableField("mid")
    private Integer mid;


}
