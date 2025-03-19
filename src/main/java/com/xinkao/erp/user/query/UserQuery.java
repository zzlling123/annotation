package com.xinkao.erp.user.query;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 用户下拉框查询
 **/
@Setter
@Getter
@ApiModel("用户下拉框查询")
public class UserQuery extends BasePageQuery implements Serializable {

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("姓名")
    private String realName;

    @ApiModelProperty("角色")
    private String roleId;

    @ApiModelProperty("是否启用")
    private String state;

    @ApiModelProperty("职务")
    private String dutie;

    @ApiModelProperty("部门ID")
    private String departmentId;

    @ApiModelProperty("部门下级IDs")
    private List<Integer> departmentIds;



}
