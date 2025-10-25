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



@Setter
@Getter
@ApiModel("用户查询")
public class UserQuery extends BasePageQuery implements Serializable {

    @ApiModelProperty("账号")
    private String username;

    @ApiModelProperty("用户名称")
    private String realName;

    @ApiModelProperty("用户角色ID")
    private String roleId;

    @ApiModelProperty("班级ID")
    private String classId;

    @ApiModelProperty("是否启用0否1是")
    private String state;

    @ApiModelProperty("当前用户角色ID（用于权限控制）")
    private Integer currentUserRoleId;
}
