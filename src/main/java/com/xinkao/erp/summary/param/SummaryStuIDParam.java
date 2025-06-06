package com.xinkao.erp.summary.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SummaryStuIDParam {

    @ApiModelProperty("是考试还是练习")
    private Integer type;
    @ApiModelProperty("学生id")
    private Integer stuId;
    @ApiModelProperty("班级id")
    private Integer classId;
    @ApiModelProperty("考试id")
    private Integer examId;

}
