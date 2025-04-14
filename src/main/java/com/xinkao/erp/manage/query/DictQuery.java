package com.xinkao.erp.manage.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DictQuery extends BasePageQuery implements Serializable {

    @ApiModelProperty("分类主键")
    private String code;

    @ApiModelProperty("班级名称")
    private String value;

}