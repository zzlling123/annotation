package com.xinkao.erp.manage.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DictQuery extends BasePageQuery implements Serializable {

    @ApiModelProperty("查询主键")
    private String type;

    @ApiModelProperty("字典值")
    private String value;

}