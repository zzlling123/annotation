package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("q_question_form_title")
public class QuestionFormTitle extends DataEntity {

    @TableField("pid")
    private Integer pid;

    @TableField("question")
    private String question;

    @TableField("question_text")
    private String questionText;

    @TableField("sort")
    private Integer sort;

    @TableField("state")
    private Integer state;

    @TableField("is_del")
    private Integer isDel;


}
