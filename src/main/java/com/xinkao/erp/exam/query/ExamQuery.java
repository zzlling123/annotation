package com.xinkao.erp.exam.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ApiModel("考试查询")
public class ExamQuery extends BasePageQuery implements Serializable {

    @ApiModelProperty("考试名称")
    private String examName;

    @ApiModelProperty("考试状态")
    private Integer status;

    @ApiModelProperty("创建者ID")
    private String createBy;

    @ApiModelProperty("创建者ID列表")
    private List<String> createByList;
}