package com.xinkao.erp.exercise.param;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubmitParam {
    @ApiModelProperty("练习项目编号")
    private Integer exerciseRecordsId;
    @ApiModelProperty("练习记录编号")
    private Integer feedbacksId;
    @ApiModelProperty("学生答案")
    private String userAnswer;
}
