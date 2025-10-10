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

    @ApiModelProperty("关联到用户的ID")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty("关联到练习模块的ID")
    @TableField("module_id")
    private Integer moduleId;

    @ApiModelProperty("shape 题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题")
    @TableField("shape")
    private Integer shape;

    @ApiModelProperty("question_score 每道题的分数")
    @TableField("question_score")
    private Integer questionScore;

    @ApiModelProperty("练习开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    @ApiModelProperty("练习结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;

    @ApiModelProperty("练习时长分钟")
    @TableField("duration")
    private Long duration;

    @ApiModelProperty("题目完成情况")
    @TableField("completion_status")
    private Integer completionStatus;

    @ApiModelProperty("练习分数")
    @TableField("score")
    private Integer score;

    @ApiModelProperty("练习反馈信息")
    @TableField("feedback")
    private String feedback;

    @ApiModelProperty("状态:0-正常 1-删除")
    @TableField("is_del")
    private Integer isDel;


}
