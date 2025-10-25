package com.xinkao.erp.summary.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamClVo {
    private Integer examId;
    private String examName;
    private Integer classId;
    private String className;
}
