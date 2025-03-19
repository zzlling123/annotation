package com.xinkao.erp.system.model.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@ApiModel("用户登录日志查询")
public class UserLoginLogQuery extends BasePageQuery implements Serializable {

    private static final long serialVersionUID = -1913605233229623933L;

    @ApiModelProperty("开始时间")
    private String strTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("登录账号")
    private String account;

    @ApiModelProperty("登录人员")
    private String realName;

    @ApiModelProperty("是否启用0否1是")
    private Integer status;
}
