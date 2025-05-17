package com.xinkao.erp.exercise.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubmitAllParam {
    @ApiModelProperty("练习项目编号")
    private Integer exerciseRecordsId;
}
