package com.xinkao.erp.exam.model.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ExamPageUserChildAnswerParam {

    @ApiModelProperty("子题Id")
    @NotBlank(message = "子题Id不能为空")
    private String childId;

    @ApiModelProperty("用户答案")
    @NotBlank(message = "用户答案不能为空")
    private String answer;
}