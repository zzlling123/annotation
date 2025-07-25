package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 题目单二级标题表
 * </p>
 *
 * @author Ldy
 * @since 2025-07-21 20:37:55
 */
@Getter
@Setter
@TableName("q_question_form_title")
public class QuestionFormTitle extends DataEntity {

    /**
     * 所属题目单ID
     */
    @TableField("pid")
    private Integer pid;

    /**
     * 题干,json列表
     */
    @TableField("question")
    private String question;

    /**
     * 题干纯文字
     */
    @TableField("question_text")
    private String questionText;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 是否可用:1-可用 0-不可用
     */
    @TableField("state")
    private Integer state;

    /**
     * 状态:0-正常 1-删除
     */
    @TableField("is_del")
    private Integer isDel;


}
