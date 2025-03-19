package com.xinkao.erp.user.vo;

import java.util.ArrayList;
import java.util.List;

import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.user.entity.Menu;

import lombok.Data;

/**
 * 菜单树
 * @author hys_thanks
 */
@Data
public class MenuRoleVo implements OutputConverter<MenuRoleVo,Menu> {
	
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
     * 是否已经勾选:0-未勾选 1-已勾选
     * **/
    private Integer isSelected = 0;
    
    /** 
     * 排序 
     * **/
    private Integer sort;
    
    /**
     * 子菜单列表
     * **/
    private List<MenuRoleVo> children = new ArrayList<MenuRoleVo>();
}
