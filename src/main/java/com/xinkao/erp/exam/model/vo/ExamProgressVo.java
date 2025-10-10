package com.xinkao.erp.exam.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExamProgressVo {

    
    private String questionId;
    
    private String num;

    private Integer shape;

    private BigDecimal score;

    @ApiModelProperty("答题状态:0-未答题 1-答题中 2-答题完成")
    private String answerStatus;
}