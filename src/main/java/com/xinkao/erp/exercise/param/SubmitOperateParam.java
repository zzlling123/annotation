package com.xinkao.erp.exercise.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubmitOperateParam {
    private Integer questionId;
    private String userAnswer;
}
