package com.xinkao.erp.scene.service.impl;

import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.scene.entity.Scene;
import com.xinkao.erp.scene.entity.ScenePcd;
import com.xinkao.erp.scene.mapper.ScenePcdMapper;
import com.xinkao.erp.scene.service.ScenePcdService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zzl
 * @since 2025-04-20 16:23:50
 */
@Service
public class ScenePcdServiceImpl extends BaseServiceImpl<ScenePcdMapper, ScenePcd> implements ScenePcdService {

    @Autowired
    private ScenePcdMapper scenePcdMapper;
    @Override
    public BaseResponse<?> save1(ScenePcd scenePcd) {
        if (lambdaQuery().eq(ScenePcd::getPcdPath, scenePcd.getPcdPath()).eq(ScenePcd::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("场景pcd文件已存在！");
        }
        return save(scenePcd) ? BaseResponse.ok("新增成功！") : BaseResponse.fail("新增失败！");
    }

    @Override
    public BaseResponse<?> update(ScenePcd scenePcd) {
        return updateById(scenePcd) ? BaseResponse.ok("修改成功！") : BaseResponse.fail("修改失败！");
    }

    @Override
    public BaseResponse<?> delete(Integer id) {
        if (lambdaQuery().eq(ScenePcd::getId, id).eq(ScenePcd::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("删除失败！");
        }
        return lambdaUpdate().eq(ScenePcd::getId, id).set(ScenePcd::getIsDel, CommonEnum.IS_DEL.YES.getCode()).update() ? BaseResponse.ok("删除成功！") : BaseResponse.fail("删除失败！");
    }

    @Override
    public ScenePcd detailById(ScenePcd scenePcd) {
        return lambdaQuery().eq(ScenePcd::getId, scenePcd.getId()).eq(ScenePcd::getIsDel, CommonEnum.IS_DEL.NO.getCode()).one();
    }
}
