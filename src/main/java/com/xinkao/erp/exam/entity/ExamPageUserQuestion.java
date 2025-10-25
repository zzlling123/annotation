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
@TableName("exam_page_user_question")
public class ExamPageUserQuestion extends DataSnowIdEntity {

    
    @TableField("user_id")
    private Integer userId;

    
    @TableField("exam_id")
    private Integer examId;

    
    @TableField("num_sort")
    private Integer numSort;

    
    @TableField("num")
    private String num;

    
    @TableField("type")
    private Integer type;

    
    @TableField("shape")
    private Integer shape;

    
    @TableField("old_question_id")
    private Integer oldQuestionId;

    
    @TableField("title")
    private String title;

    
    @TableField("question")
    private String question;

    
    @TableField("json_url")
    private String jsonUrl;

    
    @TableField("options")
    private String options;

    
    @TableField("answer_count")
    private Integer answerCount;

    
    @TableField("score")
    private BigDecimal score;

    
    @TableField("score_part")
    private BigDecimal scorePart;

    
    @TableField("answer")
    private String answer;

    
    @TableField("need_correct")
    private Integer needCorrect;

    
    @TableField("difficulty_level")
    private Integer difficultyLevel;

    
    @TableField("symbol")
    private String symbol;

    
    @TableField(exist = false)
    private String userAnswer;

    @TableField("is_form")
    private Integer isForm;

    
    @TableField("file_url")
    private String fileUrl;


}
