package com.xinkao.erp.user.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * 个人中心练习/考试柱状图查询
 **/
@Setter
@Getter
@ApiModel("个人中心练习/考试柱状图查询")
public class ExamAndPracticeBarQuery implements Serializable {

    @ApiModelProperty("1练习2考试")
    private String queryType;

    @ApiModelProperty("查询开始时间")
    private String startTime;

    @ApiModelProperty("查询结束时间")
    private String endTime;
}
