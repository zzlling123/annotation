package com.xinkao.erp.scene.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.scene.entity.ScenePcd;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.scene.query.ScenePcdQuery;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzl
 * @since 2025-04-20 16:23:50
 */
public interface ScenePcdService extends BaseService<ScenePcd> {
    Page<ScenePcd> page(ScenePcdQuery query, Pageable pageable);
    BaseResponse<?> save1(ScenePcd scenePcd);
    BaseResponse<?> update(ScenePcd scenePcd);
    BaseResponse<?> delete(Integer id);
    ScenePcd detailById(ScenePcd scenePcd);
}
