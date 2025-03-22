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
 * 题目--标签关联表
 * </p>
 *
 * @author Ldy
 * @since 2025-03-22 11:19:40
 */
@Getter
@Setter
@TableName("q_question_label")
public class QuestionLabel extends BaseEntity {

    /**
     * 题目ID
     */
    @TableField("qid")
    private Integer qid;

    /**
     * 标签ID
     */
    @TableField("lid")
    private Integer lid;


}
