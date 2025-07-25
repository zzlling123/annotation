package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 题目单子题表
 * </p>
 *
 * @author Ldy
 * @since 2025-07-21 20:37:55
 */
@Getter
@Setter
@TableName("q_question_child")
public class QuestionChild extends DataEntity {

    /**
     * 所属题目单ID
     */
    @TableField("question_id")
    private Integer questionId;

    /**
     * 所属题目单二级列表ID
     */
    @TableField("pid")
    private Integer pid;

    /**
     * 操作题标头
     */
    @TableField("title")
    private String title;

    /**
     * 题干,json列表
     */
    @TableField("question")
    private String question;

    /**
     * 文本框默认展示文字
     */
    @TableField("default_text")
    private String defaultText;

    /**
     * 题干纯文字
     */
    @TableField("question_text")
    private String questionText;

    /**
     * 是否上传文件题0否1是
     */
    @TableField("is_file")
    private Integer isFile;

    /**
     * 文件后缀
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 答案A
     */
    @TableField("answer")
    private String answer;

    /**
     * 答案说明
     */
    @TableField("answer_tip")
    private String answerTip;

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
