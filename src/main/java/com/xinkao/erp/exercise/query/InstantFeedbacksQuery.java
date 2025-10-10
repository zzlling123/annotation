package com.xinkao.erp.exercise.query;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@TableName("instant_feedbacks")
public class InstantFeedbacksQuery  extends BasePageQuery implements Serializable {

    @TableField("record_id")
    private Integer recordId;

    @TableField("question_id")
    private Integer questionId;

    @TableField("type")
    private Integer type;

    @TableField("shape")
    private Integer shape;

    @TableField("user_answer")
    private String userAnswer;

    @TableField("user_score")
    private Integer userScore;

    @TableField("correct_answer")
    private String correctAnswer;

    @TableField("is_correct")
    private Integer isCorrect;

    @TableField("is_del")
    private Integer isDel;


    private List<Integer> userId;

    @TableField(exist = false)
    private String realName;

    @TableField("finished_state")
    private Integer finishedState;

    private String startTime;

    private String endTime;

}
