package com.xinkao.erp.scene.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.scene.entity.Scene;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.scene.query.SceneQuery;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzl
 * @since 2025-04-12 23:11:56
 */
public interface SceneService extends BaseService<Scene> {
    Page<Scene> page(SceneQuery query, Pageable pageable);
    BaseResponse<?> save1(Scene scene);
    BaseResponse<?> update(Scene scene);
    BaseResponse<?> delete(Integer id);
    Scene detailById(Scene scene);
}
