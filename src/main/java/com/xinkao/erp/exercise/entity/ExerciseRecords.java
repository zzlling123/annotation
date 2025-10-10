package com.xinkao.erp.exercise.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("exercise_records")
public class ExerciseRecords extends DataEntity {

    @TableField("user_id")
    private Integer userId;

    @TableField("module_id")
    private Integer moduleId;

    @TableField("shape")
    private Integer shape;

    @TableField("question_score")
    private Integer questionScore;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("duration")
    private Long duration;

    @TableField("completion_status")
    private Integer completionStatus;

    @TableField("score")
    private Integer score;

    @TableField("feedback")
    private String feedback;

    @TableField("is_del")
    private Integer isDel;


}
