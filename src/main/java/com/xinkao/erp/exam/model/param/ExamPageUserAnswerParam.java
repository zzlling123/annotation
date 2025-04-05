package com.xinkao.erp.exam.model.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ExamPageUserAnswerParam {

    @ApiModelProperty("考试Id")
    @NotBlank(message = "考试Id不能为空")
    private String examId;

    @ApiModelProperty("题目Id")
    @NotBlank(message = "题目Id不能为空")
    private String questionId;

    @ApiModelProperty("用户答案")
    @NotBlank(message = "用户答案不能为空")
    private String answer;
}