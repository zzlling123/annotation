package com.xinkao.erp.exam.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel("批改参数")
public class ExamCorrectChildParam {

    @ApiModelProperty("子题ID")
    @NotBlank(message = "子题ID不能为空")
    private String childQuestionId;

    @ApiModelProperty("批改分数")
    @NotBlank(message = "批改分数不能为空")
    private String score;
}