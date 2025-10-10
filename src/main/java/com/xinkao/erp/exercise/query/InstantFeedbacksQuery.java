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

    @ApiModelProperty("关联到练习记录的ID")
    @TableField("record_id")
    private Integer recordId;

    @ApiModelProperty("关联到题目的ID")
    @TableField("question_id")
    private Integer questionId;

    @ApiModelProperty("标注类型")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("题目的标注类型")
    @TableField("shape")
    private Integer shape;

    @ApiModelProperty("用户的答案")
    @TableField("user_answer")
    private String userAnswer;

    @ApiModelProperty("用户得分")
    @TableField("user_score")
    private Integer userScore;

    @ApiModelProperty("正确答案")
    @TableField("correct_answer")
    private String correctAnswer;

    @ApiModelProperty("用户答案是否正确：0-错误 1-正确 2-部分正确 3-未作答")
    @TableField("is_correct")
    private Integer isCorrect;

    @ApiModelProperty("状态:0-正常 1-删除")
    @TableField("is_del")
    private Integer isDel;


    @ApiModelProperty("用户ID")
    private List<Integer> userId;

    @ApiModelProperty("用户姓名")
    @TableField(exist = false)
    private String realName;

    @ApiModelProperty("完成状态:1-进行中 2-已完成")
    @TableField("finished_state")
    private Integer finishedState;

    @ApiModelProperty("查询开始时间")
    private String startTime;

    @ApiModelProperty("查询结束时间")
    private String endTime;

}
