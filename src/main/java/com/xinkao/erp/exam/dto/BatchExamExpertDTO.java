package com.xinkao.erp.exam.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("批量添加考试专家关联")
public class BatchExamExpertDTO {
    
    @ApiModelProperty("考试ID")
    private Integer examId;
    
    @ApiModelProperty("专家ID列表")
    private List<Integer> expertIds;
}

