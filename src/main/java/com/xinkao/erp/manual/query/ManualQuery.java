package com.xinkao.erp.manual.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@ApiModel("使用文档查询")
public class ManualQuery extends BasePageQuery implements Serializable {

    @ApiModelProperty("使用人群类型")
    private Integer userType;

    @ApiModelProperty("创建用户")
    private String createBy;
} 