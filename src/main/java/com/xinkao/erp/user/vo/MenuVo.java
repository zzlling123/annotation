package com.xinkao.erp.user.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 菜单信息
 * @author hys_thanks
 */
@Data
public class MenuVo {
	 /**
     * 主键
     */
    private String menuId;
    /**
     * 父级主键
     */
    private String parentId;

    /**
     * 菜单名称
     */
    private String title;
    /**
     * 路由
     */
    private String icon;
    /**
     * 路由
     */
    private String name;
    /**
     * 路由
     */
    private String path;
    /**
     * 子菜单
     */
    private List<MenuVo> children = new ArrayList<MenuVo>();
}
