package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 题目单二级标题表
 * </p>
 *
 * @author Ldy
 * @since 2025-07-21 20:38:13
 */
@Getter
@Setter
@TableName("exam_page_user_question_form_title")
public class ExamPageUserQuestionFormTitle extends DataSnowIdEntity {

    /**
     * 试卷主键
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 考试项目主键
     */
    @TableField("exam_id")
    private Integer examId;

    /**
     * 所属题目单ID
     */
    @TableField("pid")
    private String pid;

    /**
     * 二级来源主键
     */
    @TableField("old_question_title")
    private Integer oldQuestionTitle;

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
     * 状态:0-正常 1-删除
     */
    @TableField("is_del")
    private Integer isDel;


}
