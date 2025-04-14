package com.xinkao.erp.scene.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.scene.entity.Scene;
import com.xinkao.erp.scene.mapper.SceneMapper;
import com.xinkao.erp.scene.query.SceneQuery;
import com.xinkao.erp.scene.service.SceneService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zzl
 * @since 2025-04-12 23:11:56
 */
@Service
public class SceneServiceImpl extends BaseServiceImpl<SceneMapper, Scene> implements SceneService {

    @Autowired
    private SceneMapper sceneMapper;

    @Override
    public Page<Scene> page(SceneQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return sceneMapper.page(pageable.toPage(),query);
    }


    @Override
    public BaseResponse<?> save1(Scene scene) {
        if (lambdaQuery().eq(Scene::getSceneName, scene.getSceneName()).eq(Scene::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("场景名称已存在！");
        }
        return save(scene) ? BaseResponse.ok("新增成功！") : BaseResponse.fail("新增失败！");
    }

    @Override
    public BaseResponse<?> update(Scene scene) {
        if (lambdaQuery().eq(Scene::getSceneName, scene.getSceneName()).eq(Scene::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("场景名称已存在！");
        }
        return updateById(scene) ? BaseResponse.ok("修改成功！") : BaseResponse.fail("修改失败！");
    }

    @Override
    public BaseResponse<?> delete(Integer id) {
        if (lambdaQuery().eq(Scene::getId, id).eq(Scene::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("删除失败！");
        }
        return lambdaUpdate().eq(Scene::getId, id).set(Scene::getIsDel, CommonEnum.IS_DEL.YES.getCode()).update() ? BaseResponse.ok("删除成功！") : BaseResponse.fail("删除失败！");
    }

    @Override
    public Scene detailById(Scene scene) {
        return getById(scene.getId());
    }
}
