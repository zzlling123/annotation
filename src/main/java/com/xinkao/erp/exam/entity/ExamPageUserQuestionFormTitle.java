package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("exam_page_user_question_form_title")
public class ExamPageUserQuestionFormTitle extends DataSnowIdEntity {

    
    @TableField("user_id")
    private Integer userId;

    
    @TableField("exam_id")
    private Integer examId;

    
    @TableField("pid")
    private String pid;

    
    @TableField("old_question_title")
    private Integer oldQuestionTitle;

    
    @TableField("question")
    private String question;

    
    @TableField("question_text")
    private String questionText;

    
    @TableField("sort")
    private Integer sort;

    
    @TableField("is_del")
    private Integer isDel;


}
