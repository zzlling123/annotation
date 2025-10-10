package com.xinkao.erp.manage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.manage.param.ClassInfoParam;
import com.xinkao.erp.manage.query.ClassInfoQuery;
import com.xinkao.erp.manage.vo.ClassInfoVo;

public interface ClassInfoService extends BaseService<ClassInfo> {

    Page<ClassInfoVo> page(ClassInfoQuery query, Pageable pageable);

    BaseResponse<?> save(ClassInfoParam classInfoParam);

    BaseResponse<?> update(ClassInfoParam classInfoParam);

    BaseResponse<?> delete(Integer id);
}