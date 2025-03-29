package com.xinkao.erp.exam.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("考试班级视图")
public class ExamClassVo {

    @ApiModelProperty("班级ID")
    private Integer classId;

    @ApiModelProperty("班级名称")
    private String className;
}