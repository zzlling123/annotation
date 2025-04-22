package com.xinkao.erp.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.user.param.MenuParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.user.entity.Menu;
import com.xinkao.erp.user.mapper.MenuMapper;
import com.xinkao.erp.user.service.MenuService;

/**
 * <p>
 * 管理端-菜单表 服务实现类
 * </p>
 *
 * @author hanhys
 * @since 2023-04-14 17:17:06
 */
@Service
public class MenuServiceImpl extends BaseServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    //根据用户权限获取用户菜单
    @Override
    public BaseResponse<List<Menu>> getList(){
        LoginUser loginUser = redisUtil.getInfoByToken();
        List<Menu> menuList = formatMenuList(menuMapper.getMenuRoleList(loginUser.getUser().getRoleId()));
        return BaseResponse.ok("成功！",menuList);
    }

    //根据用户权限获取用户菜单
    @Override
    public BaseResponse<List<Menu>> getAllList(){
        List<Menu> menuList = formatMenuList(lambdaQuery().eq(Menu::getIsDel,0).list());
        return BaseResponse.ok("成功！",menuList);
    }

    //递归获取子集列表
    /**
     * 格式化菜单列表
     *
     * @param menuList 菜单列表
     * @return 格式化后的菜单列表
     */
    @Override
    public List<Menu> formatMenuList(List<Menu> menuList) {
        List<Menu> formatMenuList = new ArrayList<>();
        for (Menu menu : menuList) {
            int menuId = menu.getMenuId();
            int pid = menu.getPid();

            // 一级菜单
            if (pid == 0) {
                List<Menu> childMenuList = new ArrayList<>();
                for (Menu childMenu : menuList) {
                    int childPid = childMenu.getPid();
                    // 二级菜单
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
        Menu menu = menuParam.convertTo();
        menuMapper.insert(menu);
        return BaseResponse.ok("成功！");
    }

    @Override
    public BaseResponse update(MenuParam menuParam) {
        Menu menu = menuParam.convertTo();
        updateById(menu);
        return BaseResponse.ok("成功！");
    }

    @Override
    public BaseResponse del(String id) {
        menuMapper.deleteById(id);
        return BaseResponse.ok("成功！");
    }
}
