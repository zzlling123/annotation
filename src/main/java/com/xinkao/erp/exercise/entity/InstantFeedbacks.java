package com.xinkao.erp.exercise.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@TableName("instant_feedbacks")
public class InstantFeedbacks extends DataEntity {

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

    @TableField("operation_duration")
    private Long operationDuration;

    @TableField("user_id")
    private Integer userId;

    @TableField(exist = false)
    private String realName;

    @TableField("finished_state")
    private Integer finishedState;


}
