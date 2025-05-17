package com.xinkao.erp.exercise.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@ApiModel("练习记录表")
public class ExerciseRecordsQuery  extends BasePageQuery implements Serializable {

    /**
     * 关联到用户的ID
     */
    @ApiModelProperty("关联到用户的ID")
    private Integer userId;

    @ApiModelProperty("用户名")
    private String realName;

    /**
     * 关联到练习模块的ID
     */
    @ApiModelProperty("关联到练习模块的ID")
    private Integer moduleId;

    /**
     * shape 题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
     */
    @ApiModelProperty("shape 题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题")
    private Integer shape;

    /**
     * question_score 每道题的分数
     */
    @ApiModelProperty("question_score 每道题的分数")
    private Integer questionScore;

    /**
     * 练习开始时间
     */
    @ApiModelProperty("练习开始时间")
    private LocalDateTime startTime;

    /**
     * 练习结束时间
     */
    @ApiModelProperty("练习结束时间")
    private LocalDateTime endTime;

    /**
     * 练习时长
     */
    @ApiModelProperty("练习时长分钟")
    private Long duration;

    /**
     * 题目完成情况
     */
    @ApiModelProperty("题目完成情况")
    private Integer completionStatus;

    /**
     * 练习分数
     */
    @ApiModelProperty("练习分数")
    private Integer score;

    /**
     * 练习反馈信息
     */
    @ApiModelProperty("练习反馈信息")
    private String feedback;

    /**
     * 状态:0-正常 1-删除
     */
    @ApiModelProperty("状态:0-正常 1-删除")
    private Integer isDel;
}
