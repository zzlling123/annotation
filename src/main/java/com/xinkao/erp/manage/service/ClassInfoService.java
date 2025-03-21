package com.xinkao.erp.manage.service;

import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.manage.param.ClassInfoParam;
import com.xinkao.erp.manage.query.ClassInfoQuery;
import com.xinkao.erp.manage.vo.ClassInfoVo;
import org.springframework.data.domain.Page;

/**
 * <p>
 * 班级表 服务类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-21 14:32:24
 */
public interface ClassInfoService extends BaseService<ClassInfo> {

    Page<ClassInfoVo> page(ClassInfoQuery query, Pageable pageable);

    BaseResponse<?> save(ClassInfoParam classInfoParam);

    BaseResponse<?> update(ClassInfoParam classInfoParam);

    BaseResponse<?> delete(Integer id);
}