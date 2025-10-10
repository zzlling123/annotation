package com.xinkao.erp.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.xinkao.erp.common.model.entity.BaseEntity;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("role")
public class Role extends DataEntity {

    @ApiModelProperty("角色名称")
    @TableField("role_name")
    private String roleName;

    @ApiModelProperty("是否删除0否1是")
    @TableField("is_del")
    private Integer isDel;


}
