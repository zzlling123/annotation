package com.xinkao.erp.exam.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@ApiModel("查询")
public class SymbolQuery extends BasePageQuery implements Serializable {

    @ApiModelProperty("名称")
    private String symbolName;
}