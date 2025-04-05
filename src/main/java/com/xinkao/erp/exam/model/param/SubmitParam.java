package com.xinkao.erp.exam.model.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SubmitParam {

    @ApiModelProperty("考试id")
    @NotBlank(message = "考试id不能为空")
    private String examId;
}