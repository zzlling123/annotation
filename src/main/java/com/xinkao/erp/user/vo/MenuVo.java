package com.xinkao.erp.user.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


@Data
public class MenuVo {
	 
    private String menuId;
    
    private String parentId;

    
    private String title;
    
    private String icon;
    
    private String name;
    
    private String path;
    
    private List<MenuVo> children = new ArrayList<MenuVo>();
}
