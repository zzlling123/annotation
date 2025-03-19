package com.xinkao.erp.system.model.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@ApiModel("用户操作日志查询")
public class UserOptLogQuery extends BasePageQuery implements Serializable {

    private static final long serialVersionUID = -1913605233229623933L;

    @ApiModelProperty("开始时间")
    private String strTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("操作账号")
    private String account;

    @ApiModelProperty("操作人员")
    private String realName;

    @ApiModelProperty("操作内容")
    private String content;

    @ApiModelProperty("是否启用0否1是")
    private String status;
}
