package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("exam_page_user_child_answer")
public class ExamPageUserChildAnswer extends DataSnowIdEntity {

    
    @TableField("user_id")
    private Integer userId;

    
    @TableField("exam_id")
    private Integer examId;

    
    @TableField("question_id")
    private String questionId;

    
    @TableField("question_child_id")
    private String questionChildId;

    
    @TableField("is_file")
    private Integer isFile;

    
    @TableField("num_sort")
    private Integer numSort;

    
    @TableField("num")
    private String num;

    
    @TableField("right_answer")
    private String rightAnswer;

    
    @TableField("score")
    private BigDecimal score;

    
    @TableField("score_part")
    private BigDecimal scorePart;

    
    @TableField("answer_status")
    private Integer answerStatus;

    
    @TableField("user_answer")
    private String userAnswer;

    
    @TableField("user_score")
    private BigDecimal userScore;

    
    @TableField("need_correct")
    private Integer needCorrect;

    
    @TableField("correct_id")
    private Integer correctId;

    
    @TableField("correct_time")
    private Date correctTime;


}
