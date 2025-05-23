package com.xinkao.erp.exam.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamProgressVo {

    /**
     * 题目ID
     */
    private String questionId;
    /**
     * 题号
     */
    private String num;

    private Integer shape;

    private Integer score;

    @ApiModelProperty("答题状态:0-未答题 1-答题中 2-答题完成")
    private String answerStatus;
}