package com.xinkao.erp.exercise.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 即时反馈表
 * </p>
 *
 * @author zzl
 * @since 2025-04-05 23:15:56
 */
@Getter
@Setter
@TableName("instant_feedbacks")
public class InstantFeedbacks extends DataEntity {

    /**
     * 关联到练习记录的ID
     */
    @ApiModelProperty("关联到练习记录的ID")
    @TableField("record_id")
    private Integer recordId;

    /**
     * 关联到题目的ID
     */
    @ApiModelProperty("关联到题目的ID")
    @TableField("question_id")
    private Integer questionId;

    /**
     * 题目的标注类型
     */
    @ApiModelProperty("题目的标注类型")
    @TableField("shape")
    private Integer shape;

    /**
     * 用户的答案
     */
    @ApiModelProperty("用户的答案")
    @TableField("user_answer")
    private String userAnswer;

    @ApiModelProperty("用户得分")
    @TableField("user_score")
    private Integer userScore;

    /**
     * 正确答案
     */
    @ApiModelProperty("正确答案")
    @TableField("correct_answer")
    private String correctAnswer;

    /**
     * 用户答案是否正确0-错误 1-正确 2-部分正确 3-未作答
     */
    @ApiModelProperty("用户答案是否正确：0-错误 1-正确 2-部分正确 3-未作答")
    @TableField("is_correct")
    private Integer isCorrect;

    /**
     * 状态:0-正常 1-删除
     */
    @ApiModelProperty("状态:0-正常 1-删除")
    @TableField("is_del")
    private Integer isDel;


}
