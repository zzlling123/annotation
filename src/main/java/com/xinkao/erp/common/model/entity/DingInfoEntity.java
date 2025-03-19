package com.xinkao.erp.common.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("字典类型表实体")
public class DingInfoEntity implements Serializable {
    @ApiModelProperty("用户ID")
    private String id;

    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("部门ID")
    private String deptId;

    @ApiModelProperty("职位")
    private String title;

    @ApiModelProperty("下级部门")
    private List<DingInfoEntity> child;

    @ApiModelProperty("是否为部门")
    private Boolean dept;

    @ApiModelProperty("是否是部门的主管")
    private Boolean leader;

}
