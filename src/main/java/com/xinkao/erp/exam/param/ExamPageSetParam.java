package com.xinkao.erp.exam.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@ApiModel("考试参数")
public class ExamPageSetParam {



    @ApiModelProperty("type_id")
    private Integer typeId;


    @ApiModelProperty("type_name")
    private String typeName;


    @ApiModelProperty("shape")
    private Integer shape;


    @ApiModelProperty("question_num")
    private Integer questionNum;


    @ApiModelProperty("score")
    private BigDecimal score;


    @ApiModelProperty("score_part")
    private BigDecimal scorePart;
}