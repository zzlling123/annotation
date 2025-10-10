package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;


@Getter
@Setter
@TableName("exam_page_user_answer")
public class ExamPageUserAnswer extends DataSnowIdEntity {

    
    @TableField("user_id")
    private Integer userId;

    
    @TableField("exam_id")
    private Integer examId;

    
    @TableField("question_id")
    private String questionId;

    
    @TableField("num_sort")
    private Integer numSort;

    
    @TableField("num")
    private String num;

    
    @TableField("type")
    private Integer type;

    
    @TableField("shape")
    private Integer shape;

    
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

    
    @TableField("correct_id")
    private Integer correctId;

    
    @TableField("correct_time")
    private Date correctTime;









    
    @TableField("biao")
    private Integer biao;

    
    @TableField("cuo")
    private Integer cuo;

    
    @TableField("wu")
    private Integer wu;

    
    @TableField("shu")
    private Integer shu;

    
    @TableField("zong")
    private Integer zong;

    
    @TableField("da")
    private Integer da;

    
    @TableField("accuracy_rate")
    private BigDecimal accuracyRate;

    @TableField("coverage_rate")
    private BigDecimal coverageRate;

    
    @TableField("need_correct")
    private Integer needCorrect;


}
