package com.xinkao.erp.summary.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamClVo {
    @ApiModelProperty("考试ID")
    private Integer examId;
    @ApiModelProperty("考试名称")
    private String examName;
    @ApiModelProperty("班级ID")
    private Integer classId;
    @ApiModelProperty("班级名称")
    private String className;
}
