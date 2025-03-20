package com.xinkao.erp.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinkao.erp.common.model.entity.DingInfoEntity;
import com.xinkao.erp.user.service.DingService;
import com.xinkao.erp.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

/**
 * <p>
 * 学校表 服务实现类
 * </p>
 *
 * @author Ldy
 * @since 2023-03-02 09:42:04
 */
@Slf4j
@Service
public class DingServiceImpl implements DingService {

    @Autowired
    private DingUtils dingUtils;
    @Autowired
    private UserService userService;

    public Map<String,List<DingInfoEntity>> getAllDingUserList(List<DingInfoEntity> userList,List<DingInfoEntity> departmentList,List<DingInfoEntity> deptList){
        for (DingInfoEntity deptEntity : deptList) {
            if (deptEntity.getDept()){
                //是部门，添加到部门组，继续循环
                DingInfoEntity departmentDto = BeanUtil.copyProperties(deptEntity,DingInfoEntity.class);
                departmentDto.setChild(null);
                departmentList.add(departmentDto);
                if (deptEntity.getChild() != null){
                    getAllDingUserList(userList,departmentList,deptEntity.getChild());
                }
            }else {
                //是个人，添加到数组
                userList.add(deptEntity);
            }
        }
        Map<String,List<DingInfoEntity>> map = new HashMap<>();
        map.put("department",departmentList);
        map.put("user",userList);
        return map;
    }


    @Override
    public boolean saveBatch(Collection entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(Object entity) {
        return false;
    }

    @Override
    public Object getOne(Wrapper queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper queryWrapper) {
        return null;
    }

    @Override
    public BaseMapper getBaseMapper() {
        return null;
    }

    @Override
    public Class getEntityClass() {
        return null;
    }

    @Override
    public Object getObj(Wrapper queryWrapper, Function mapper) {
        return null;
    }
}
