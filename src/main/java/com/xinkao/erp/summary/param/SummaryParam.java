package com.xinkao.erp.summary.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SummaryParam {
    //是考试还是练习
    @ApiModelProperty("是考试还是练习")
    private Integer type;
    //班级id
     @ApiModelProperty("班级id")
    private Integer classId;
    //考试id
     @ApiModelProperty("考试id")
    private Integer examId;
     //题id
    //@ApiModelProperty("题id")
    //private Integer questionId;

//    @ApiModelProperty("查询开始时间")
//    private String startTime;
//
//    @ApiModelProperty("查询结束时间")
//    private String endTime;
}
