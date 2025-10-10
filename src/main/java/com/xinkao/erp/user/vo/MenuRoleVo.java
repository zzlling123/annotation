package com.xinkao.erp.user.vo;

import java.util.ArrayList;
import java.util.List;

import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.user.entity.Menu;

import lombok.Data;


@Data
public class MenuRoleVo implements OutputConverter<MenuRoleVo,Menu> {
	
	 
    private String menuId;
    
    private String parentId;

    
    private String title;
    
    private String icon;
    
    private String name;
    
    private String path;
    
    
    private Integer isSelected = 0;
    
    
    private Integer sort;
    
    
    private List<MenuRoleVo> children = new ArrayList<MenuRoleVo>();
}
