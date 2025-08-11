package com.xinkao.erp.question.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class QuestionExercisePageVo extends BaseEntity {

    @ApiModelProperty("题目Id")
    private String id;

    @ApiModelProperty("操作题标头")
    private String title;

    @ApiModelProperty("题目")
    private String question;

    @ApiModelProperty("题目text")
    private String questionText;

    @TableField("题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题")
    private String shape;

    @ApiModelProperty("题型")
    private String type;

    @ApiModelProperty("难度")
    private String difficultyLevel;

    @ApiModelProperty("是否启用0否1是")
    private Integer state;

    @ApiModelProperty("所属范围1人社局2学校")
    private Integer symbol;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @ApiModelProperty("是否需要批改0否1是")
    private Integer needCorrect;

    @ApiModelProperty("是否练习用题0否1是")
    private Integer forExercise;

    @ApiModelProperty("文档路径")
    private String fileUrl;

    @ApiModelProperty("题目的预计用时(分钟)")
    private Integer estimatedTime;

    @ApiModelProperty("练习状态:0未开始，1进行中，2已完成")
    private Integer exerciseState;
}