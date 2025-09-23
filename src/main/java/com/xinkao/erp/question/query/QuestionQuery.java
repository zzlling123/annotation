package com.xinkao.erp.question.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@ApiModel("题库查询")
public class QuestionQuery extends BasePageQuery implements Serializable {

    @ApiModelProperty("题目")
    private String question;

    @ApiModelProperty("分类")
    private String type;

    @ApiModelProperty("题型")
    private String shape;

    @ApiModelProperty("难度")
    private String difficultyLevel;

    @ApiModelProperty("是否启用0否1是")
    private String state;

    @ApiModelProperty("是否为练习题0否1是")
    private Integer forExercise;

    @ApiModelProperty("所属范围1人社局2学校")
    private Integer symbol;

    @ApiModelProperty("班级id")
    private Integer classId;

    @ApiModelProperty("自定义标签ID")
    private Integer labelId;
}