package com.xinkao.erp.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 角色菜单表
 * </p>
 *
 * @author Ldy
 * @since 2024-04-28 09:09:10
 */
@Getter
@Setter
@TableName("role_menu")
public class RoleMenu extends BaseEntity {

    @ApiModelProperty("角色ID")
    @TableId("role_id")
    private Integer roleId;

    @ApiModelProperty("菜单ID")
    @TableField("menu_id")
    private Integer menuId;


    public RoleMenu(Integer roleId, Integer menuId) {
        this.roleId = roleId;
        this.menuId = menuId;
    }
}
