package com.xinkao.erp.manage.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MarkQuery implements Serializable {

    @ApiModelProperty("分类ID")
    private String type;
}