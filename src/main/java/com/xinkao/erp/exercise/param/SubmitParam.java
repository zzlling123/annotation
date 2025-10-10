package com.xinkao.erp.exercise.param;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubmitParam {
    private Integer exerciseRecordsId;
    private Integer questionId;
    private String userAnswer;
}
