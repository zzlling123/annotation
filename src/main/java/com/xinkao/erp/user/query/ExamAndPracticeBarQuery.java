package com.xinkao.erp.user.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Setter
@Getter
public class ExamAndPracticeBarQuery implements Serializable {

    private String queryType;

    private String startTime;

    private String endTime;
}
