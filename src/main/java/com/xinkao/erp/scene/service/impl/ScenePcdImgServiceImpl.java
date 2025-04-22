package com.xinkao.erp.scene.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.scene.entity.ScenePcdImg;
import com.xinkao.erp.scene.mapper.ScenePcdImgMapper;
import com.xinkao.erp.scene.query.ScenePcdImgQuery;
import com.xinkao.erp.scene.service.ScenePcdImgService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author zzl
 * @since 2025-04-20 16:23:50
 */
@Service
public class ScenePcdImgServiceImpl extends BaseServiceImpl<ScenePcdImgMapper, ScenePcdImg> implements ScenePcdImgService {

    @Autowired
    private ScenePcdImgMapper scenePcdImgMapper;

    @Override
    public Page<ScenePcdImg> page(ScenePcdImgQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return scenePcdImgMapper.page(page, query);
    }

    @Override
    public BaseResponse<?> save1(ScenePcdImg scenePcdImg) {
        if (lambdaQuery().eq(ScenePcdImg::getPcdId, scenePcdImg.getPcdId()).eq(ScenePcdImg::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0){
            return BaseResponse.fail("场景pcd图片已存在！");
        }
        return save(scenePcdImg) ? BaseResponse.ok("新增成功！") : BaseResponse.fail("新增失败！");
    }

    @Override
    public BaseResponse<?> update(ScenePcdImg scenePcdImg) {
        if (lambdaQuery().eq(ScenePcdImg::getPcdId, scenePcdImg.getPcdId()).eq(ScenePcdImg::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0){
            return BaseResponse.fail("场景pcd图片已存在！");
        }
        return updateById(scenePcdImg) ? BaseResponse.ok("修改成功！") : BaseResponse.fail("修改失败！");
    }

    @Override
    public BaseResponse<?> delete(Integer id) {
        if (lambdaQuery().eq(ScenePcdImg::getId, id).eq(ScenePcdImg::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("删除失败！");
        }
        return lambdaUpdate().eq(ScenePcdImg::getId, id).set(ScenePcdImg::getIsDel, CommonEnum.IS_DEL.YES.getCode()).update() ? BaseResponse.ok("删除成功！") : BaseResponse.fail("删除失败！");
    }

    @Override
    public ScenePcdImg detailById(ScenePcdImg scenePcdImg) {
        return lambdaQuery().eq(ScenePcdImg::getId, scenePcdImg.getId()).eq(ScenePcdImg::getIsDel, CommonEnum.IS_DEL.NO.getCode()).one();
    }
}
