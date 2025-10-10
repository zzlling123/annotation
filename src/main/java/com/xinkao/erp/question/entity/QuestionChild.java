package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("q_question_child")
public class QuestionChild extends DataEntity {

    @TableField("question_id")
    private Integer questionId;

    @TableField("pid")
    private Integer pid;

    @TableField("title")
    private String title;

    @TableField("question")
    private String question;

    @TableField("default_text")
    private String defaultText;

    @TableField("question_text")
    private String questionText;

    @TableField("is_file")
    private Integer isFile;

    @TableField("file_type")
    private String fileType;

    @TableField("answer")
    private String answer;

    @TableField("answer_tip")
    private String answerTip;

    @TableField("sort")
    private Integer sort;

    @TableField("state")
    private Integer state;

    @TableField("is_del")
    private Integer isDel;


}
