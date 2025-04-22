package com.xinkao.erp.scene.service;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.scene.entity.ScenePcd;
import com.xinkao.erp.common.service.BaseService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzl
 * @since 2025-04-20 16:23:50
 */
public interface ScenePcdService extends BaseService<ScenePcd> {
    BaseResponse<?> save1(ScenePcd scenePcd);
    BaseResponse<?> update(ScenePcd scenePcd);
    BaseResponse<?> delete(Integer id);
    ScenePcd detailById(ScenePcd scenePcd);
}
