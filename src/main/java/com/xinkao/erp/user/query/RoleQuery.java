package com.xinkao.erp.user.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * 角色查询
 **/
@Setter
@Getter
@ApiModel("角色分页查询")
public class RoleQuery extends BasePageQuery implements Serializable {

    private static final long serialVersionUID = -1913605233229623933L;


    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("创建人姓名")
    private String createName;
}
