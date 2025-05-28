package com.xinkao.erp.exercise.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubmitOperateParam {
    @ApiModelProperty("练习题目编号")
    private Integer questionId;
    @ApiModelProperty("学生答案")
    private String userAnswer;
}
