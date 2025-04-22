package com.xinkao.erp.scene.service;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.scene.entity.ScenePcdImg;
import com.xinkao.erp.common.service.BaseService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzl
 * @since 2025-04-20 16:23:50
 */
public interface ScenePcdImgService extends BaseService<ScenePcdImg> {
    BaseResponse<?> save1(ScenePcdImg scenePcdImg);
    BaseResponse<?> update(ScenePcdImg scenePcdImg);
    BaseResponse<?> delete(Integer id);
    ScenePcdImg detailById(ScenePcdImg scenePcdImg);

}
