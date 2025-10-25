package com.xinkao.erp.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.user.param.MenuParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.user.entity.Menu;
import com.xinkao.erp.user.mapper.MenuMapper;
import com.xinkao.erp.user.service.MenuService;

@Service
public class MenuServiceImpl extends BaseServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public BaseResponse<List<Menu>> getList(){
        LoginUser loginUser = redisUtil.getInfoByToken();
        List<Menu> menuList = formatMenuList(menuMapper.getMenuRoleList(loginUser.getUser().getRoleId()));
        return BaseResponse.ok("成功！",menuList);
    }

    @Override
    public BaseResponse<List<Menu>> getAllList(){
        List<Menu> menuList = formatMenuList(lambdaQuery().eq(Menu::getIsDel,0).list());
        return BaseResponse.ok("成功！",menuList);
    }

    @Override
    public List<Menu> formatMenuList(List<Menu> menuList) {
        List<Menu> formatMenuList = new ArrayList<>();
        for (Menu menu : menuList) {
            int menuId = menu.getMenuId();
            int pid = menu.getPid();

            if (pid == 0) {
                List<Menu> childMenuList = new ArrayList<>();
                for (Menu childMenu : menuList) {
                    int childPid = childMenu.getPid();
                    if (childPid == menuId) {
                        childMenuList.add(childMenu);
                    }
                }
                menu.setChildMenuList(childMenuList);
                formatMenuList.add(menu);
            }
        }

        return formatMenuList;
    }

    @Override
    public BaseResponse save(MenuParam menuParam) {
        Menu menu = BeanUtil.copyProperties(menuParam, Menu.class);
        menuMapper.insert(menu);
        return BaseResponse.ok("成功！");
    }

    @Override
    public BaseResponse update(MenuParam menuParam) {
        Menu menu = BeanUtil.copyProperties(menuParam, Menu.class);
        updateById(menu);
        return BaseResponse.ok("成功！");
    }

    @Override
    public BaseResponse del(String id) {
        menuMapper.deleteById(id);
        return BaseResponse.ok("成功！");
    }
}
