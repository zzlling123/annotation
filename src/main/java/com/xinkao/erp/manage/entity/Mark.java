package com.xinkao.erp.manage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import com.xinkao.erp.user.entity.Menu;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@TableName("mark")
public class Mark extends DataEntity {


    @TableField("type")
    private Integer type;


    @TableField("type_name")
    private String typeName;


    @TableField("mark_name")
    private String markName;


    @TableField("value")
    private String value;


    @TableField("pid")
    private Integer pid;

    @ApiModelProperty("父级ID列表")
    @TableField("parent")
    private String parent;

    @ApiModelProperty("父级名称列表")
    @TableField("parent_route")
    private String parentRoute;


    @TableField("sort")
    private Integer sort;


    @TableField("state")
    private Integer state;

    @ApiModelProperty("是否删除0否1是")
    @TableField("is_del")
    private Integer isDel;

    @ApiModelProperty("子集菜单")
    @TableField(exist = false)
    private List<Mark> childMarkList;


}
