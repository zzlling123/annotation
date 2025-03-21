package com.xinkao.erp.manage.service.impl;

import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.manage.mapper.ClassInfoMapper;
import com.xinkao.erp.manage.param.ClassInfoParam;
import com.xinkao.erp.manage.query.ClassInfoQuery;
import com.xinkao.erp.manage.service.ClassInfoService;
import com.xinkao.erp.manage.vo.ClassInfoVo;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClassInfoServiceImpl extends BaseServiceImpl<ClassInfoMapper, ClassInfo> implements ClassInfoService {

    @Autowired
    private ClassInfoMapper classInfoMapper;

    @Override
    public Page<ClassInfoVo> page(ClassInfoQuery query, Pageable pageable) {
        // 实现分页查询逻辑
        log.info("Executing page query with query: {} and pageable: {}", query, pageable);
        return null;
    }

    @Override
    public BaseResponse<?> save(ClassInfoParam classInfoParam) {
        // 实现新增逻辑
        log.info("Saving class info with param: {}", classInfoParam);
        return null;
    }

    @Override
    public BaseResponse<?> update(ClassInfoParam classInfoParam) {
        // 实现编辑逻辑
        log.info("Updating class info with param: {}", classInfoParam);
        return null;
    }

    @Override
    public BaseResponse<?> delete(Integer id) {
        // 实现删除逻辑
        log.info("Deleting class info with id: {}", id);
        return null;
    }
}