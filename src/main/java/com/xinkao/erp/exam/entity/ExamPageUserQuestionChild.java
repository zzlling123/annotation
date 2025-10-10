package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@TableName("exam_page_user_question_child")
public class ExamPageUserQuestionChild extends DataSnowIdEntity {

    
    @TableField("question_id")
    private String questionId;

    
    @TableField("pid")
    private String pid;

    
    @TableField("user_id")
    private Integer userId;

    
    @TableField("exam_id")
    private Integer examId;

    
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

    @TableField(exist = false)
    private BigDecimal score;

    @TableField(exist = false)
    private Integer needCorrect;


}
