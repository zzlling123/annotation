package com.xinkao.erp.exam.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("考试参数")
public class ExamPageSetParam {


    /**
     * 分类ID
     */
    @ApiModelProperty("type_id")
    private Integer typeId;

    /**
     * 分类名称
     */
    @ApiModelProperty("type_name")
    private String typeName;

    /**
     * 题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
     */
    @ApiModelProperty("shape")
    private Integer shape;

    /**
     * 该分类下该种题型题目数量
     */
    @ApiModelProperty("question_num")
    private Integer questionNum;

    /**
     * 每题分数
     */
    @ApiModelProperty("score")
    private Integer score;

    /**
     * 部分得分
     */
    @ApiModelProperty("score_part")
    private Integer scorePart;
}