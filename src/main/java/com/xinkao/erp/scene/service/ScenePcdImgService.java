package com.xinkao.erp.scene.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.scene.entity.ScenePcdImg;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.scene.query.ScenePcdImgQuery;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzl
 * @since 2025-04-20 16:23:50
 */
public interface ScenePcdImgService extends BaseService<ScenePcdImg> {
    Page<ScenePcdImg> page(ScenePcdImgQuery query, Pageable pageable);
    BaseResponse<?> save1(ScenePcdImg scenePcdImg);
    BaseResponse<?> update(ScenePcdImg scenePcdImg);
    BaseResponse<?> delete(Integer id);
    ScenePcdImg detailById(ScenePcdImg scenePcdImg);

}
