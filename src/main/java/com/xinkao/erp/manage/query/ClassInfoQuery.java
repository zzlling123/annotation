package com.xinkao.erp.manage.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ClassInfoQuery extends BasePageQuery implements Serializable {

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("班主任ID（非管理员时由后端注入）")
    private Integer directorId;

    @ApiModelProperty("班主任ID列表")
    private List<Integer> directorIdList;

}