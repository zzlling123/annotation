package com.xinkao.erp.user.entity;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.entity.BaseEntity;

import com.xinkao.erp.common.model.entity.DataNoIdEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 管理端-菜单表
 * </p>
 *
 * @author hanhys
 * @since 2023-08-03 14:10:39
 */
@Getter
@Setter
@TableName("menu")
public class Menu extends BaseEntity {
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Integer menuId;

    @ApiModelProperty("菜单名称")
    @TableField("menu_name")
    private String menuName;

    @ApiModelProperty("父级菜单")
    @TableField("pid")
    private Integer pid;

    @ApiModelProperty("图片")
    @TableField("icon")
    private String icon;

    @ApiModelProperty("路由路径")
    @TableField("route")
    private String route;

    @ApiModelProperty("排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty("是否删除0否1是")
    @TableField("is_del")
    private Integer isDel;

    @ApiModelProperty("是否已选中")
    @TableField(exist = false)
    private Integer checked = 0;

    @ApiModelProperty("子集菜单")
    @TableField(exist = false)
    private List<Menu> childMenuList;
}
