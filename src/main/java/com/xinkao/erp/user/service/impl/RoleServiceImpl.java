package com.xinkao.erp.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.login.service.UserOptLogService;
import com.xinkao.erp.user.entity.Menu;
import com.xinkao.erp.user.entity.RoleMenu;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.mapper.MenuMapper;
import com.xinkao.erp.user.param.RoleParam;
import com.xinkao.erp.user.query.RoleQuery;
import com.xinkao.erp.user.service.MenuService;
import com.xinkao.erp.user.service.RoleMenuService;
import com.xinkao.erp.user.service.UserService;
import com.xinkao.erp.user.vo.RolePageVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.user.entity.Role;
import com.xinkao.erp.user.mapper.RoleMapper;
import com.xinkao.erp.user.query.RolePageQuery;
import com.xinkao.erp.user.service.RoleService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 管理端-角色表 服务实现类
 * </p>
 *
 * @author hanhys
 * @since 2023-03-29 13:19:13
 */
@Service
public class RoleServiceImpl extends BaseServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserOptLogService userOptLogService;
    //分页
    @Override
    public Page<RolePageVo> page(RoleQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return roleMapper.page(page, query);
    }

    //根据id获取角色权限列表
    @Override
    public BaseResponse<List<Menu>> getRoleMenuList(RoleParam roleParam){
        //获取该企业下超级管理员的权限所有菜单
        LoginUser loginUser = redisUtil.getInfoByToken();
        User user = loginUser.getUser();
        List<Menu> menuList = menuService.lambdaQuery().eq(Menu::getIsDel,0).list();
        if (roleParam.getId() != null && !"".equals(roleParam.getId())){
            //获取该角色下的菜单是否已选中
            List<Integer> roleMenuIds = roleMenuService.lambdaQuery().eq(RoleMenu::getRoleId,roleParam.getId()).list().stream().map(RoleMenu::getMenuId)
                    .collect(Collectors.toList());
            for (Menu menu : menuList) {
                if (roleMenuIds.contains(menu.getMenuId())){
                    menu.setChecked(1);
                }else{
                    menu.setChecked(0);
                }
            }
        }
        menuList = menuService.formatMenuList(menuList);
        return BaseResponse.ok("成功！",menuList);
    }

    //新增
    @Transactional
    @Override
    public BaseResponse save(RoleParam roleParam){
        LoginUser loginUser = redisUtil.getInfoByToken();
        //新增角色
        Role role = BeanUtil.copyProperties(roleParam,Role.class);
        role.setCreateBy(loginUser.getUser().getRealName());
        role.setCreateTime(DateUtil.date());
        if (lambdaQuery().eq(Role::getRoleName,role.getRoleName()).eq(Role::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0){
            return BaseResponse.fail("角色名称已存在！");
        }
        save(role);
        //新增角色菜单关联关系
        if (roleParam.getMenuIds() != null && roleParam.getMenuIds().size() > 0){
            List<RoleMenu> roleMenus = new ArrayList<>();
            for (String menuId : roleParam.getMenuIds()) {
                roleMenus.add(new RoleMenu(role.getId(),Integer.valueOf(menuId)));
            }
            roleMenuService.saveBatch(roleMenus);
        }
        userOptLogService.saveLog("新增角色,名称："+roleParam.getRoleName(), JSON.toJSONString(roleParam));
        return BaseResponse.ok("新增成功！");
    }

    //修改
    @Transactional
    @Override
    public BaseResponse update(RoleParam roleParam){
        LoginUser loginUser = redisUtil.getInfoByToken();
        if (roleParam.getId() == null || roleParam.getId().equals("")){
            return BaseResponse.fail("参数错误,id不可为空！");
        }
        //新增角色
        Role roleOld = getById(roleParam.getId());
        Role role = BeanUtil.copyProperties(roleParam,Role.class);
        role.setUpdateBy(loginUser.getUser().getRealName());
        role.setUpdateTime(DateUtil.date());
        if (lambdaQuery().eq(Role::getRoleName,role.getRoleName()).eq(Role::getIsDel, CommonEnum.IS_DEL.NO.getCode()).ne(Role::getId,role.getId()).count() > 0){
            return BaseResponse.fail("角色名称已存在！");
        }
        updateById(role);
        //清除之前的角色菜单关联关系
        roleMenuService.lambdaUpdate()
                .eq(RoleMenu::getRoleId,roleParam.getId())
                .remove();
        //新增角色菜单关联关系
        if (roleParam.getMenuIds() != null && roleParam.getMenuIds().size() > 0){
            List<RoleMenu> roleMenus = new ArrayList<>();
            for (String menuId : roleParam.getMenuIds()) {
                roleMenus.add(new RoleMenu(role.getId(),Integer.valueOf(menuId)));
            }
            roleMenuService.saveBatch(roleMenus);
        }
        userOptLogService.saveLog("编辑角色,名称："+roleOld.getRoleName(), JSON.toJSONString(roleParam));
        return BaseResponse.ok("修改成功！");
    }

    //删除
    @Transactional
    @Override
    public BaseResponse del(Integer roleId){
        LoginUser loginUser = redisUtil.getInfoByToken();
        //验证是否有用户使用该角色
        if (userService.lambdaQuery().eq(User::getRoleId,roleId).eq(User::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0){
            return BaseResponse.fail("该角色下有用户信息，请先处理用户信息再执行该操作！");
        }
        Role roleOld = getById(roleId);
        Role role = new Role();
        role.setId(roleId);
        role.setIsDel(CommonEnum.IS_DEL.YES.getCode());
        role.setUpdateBy(loginUser.getUser().getRealName());
        role.setUpdateTime(DateUtil.date());
        userOptLogService.saveLog("删除角色,名称："+roleOld.getRoleName(), JSON.toJSONString(roleId));
        //删除角色
        return updateById(role)?BaseResponse.ok("删除成功！"): BaseResponse.fail("删除失败！");
    }

    @Override
    public List<Role> getRoleList() {
        LoginUser loginUser = redisUtil.getInfoByToken();
        Integer currentUserRoleId = loginUser.getUser().getRoleId();

        // 如果是角色18，只返回角色2和3的信息
        if (currentUserRoleId == 18) {
            return lambdaQuery()
                    .eq(Role::getIsDel, CommonEnum.IS_DEL.NO.getCode())
                    .in(Role::getId, Arrays.asList(2, 3))
                    .list();
        }

        // 如果是角色19，只返回角色20和21的信息
        if (currentUserRoleId == 19) {
            return lambdaQuery()
                    .eq(Role::getIsDel, CommonEnum.IS_DEL.NO.getCode())
                    .in(Role::getId, Arrays.asList(20, 21))
                    .list();
        }

        // 其他角色返回所有角色列表
        return lambdaQuery()
                .eq(Role::getIsDel, CommonEnum.IS_DEL.NO.getCode())
                .list();
    }
}
