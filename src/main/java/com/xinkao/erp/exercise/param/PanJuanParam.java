package com.xinkao.erp.exercise.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class PanJuanParam {

    private Integer isCorrect;

    private Integer biao;

    private Integer cuo;

    private Integer wu;

    private Integer shu;

    private Integer zong;

    private Integer da;

    private BigDecimal accuracyRate;

    private BigDecimal coverageRate;

    private Long operationDuration;

    private BigDecimal score;
}
