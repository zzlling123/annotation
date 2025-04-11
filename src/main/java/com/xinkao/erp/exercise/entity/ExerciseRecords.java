package com.xinkao.erp.exercise.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 练习记录表
 * </p>
 *
 * @author zzl
 * @since 2025-04-05 23:15:56
 */
@Getter
@Setter
@TableName("exercise_records")
public class ExerciseRecords extends DataEntity {

    /**
     * 关联到用户的ID
     */
    @ApiModelProperty("关联到用户的ID")
    @TableField("user_id")
    private Integer userId;

    /**
     * 关联到练习模块的ID
     */
    @ApiModelProperty("关联到练习模块的ID")
    @TableField("module_id")
    private Integer moduleId;

    /**
     * shape 题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
     */
    @ApiModelProperty("shape 题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题")
    @TableField("shape")
    private Integer shape;

    /**
     * question_score 每道题的分数
     */
    @ApiModelProperty("question_score 每道题的分数")
    @TableField("question_score")
    private Integer questionScore;

    /**
     * 练习开始时间
     */
    @ApiModelProperty("练习开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 练习结束时间
     */
    @ApiModelProperty("练习结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 练习时长
     */
    @ApiModelProperty("练习时长分钟")
    @TableField("duration")
    private Long duration;

    /**
     * 题目完成情况
     */
    @ApiModelProperty("题目完成情况")
    @TableField("completion_status")
    private Integer completionStatus;

    /**
     * 练习分数
     */
    @ApiModelProperty("练习分数")
    @TableField("score")
    private Integer score;

    /**
     * 练习反馈信息
     */
    @ApiModelProperty("练习反馈信息")
    @TableField("feedback")
    private String feedback;

    /**
     * 状态:0-正常 1-删除
     */
    @ApiModelProperty("状态:0-正常 1-删除")
    @TableField("is_del")
    private Integer isDel;


}
