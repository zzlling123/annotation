package com.xinkao.erp.scene.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.scene.entity.Scene;
import com.xinkao.erp.scene.entity.ScenePcdImg;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.scene.query.ScenePcdImgQuery;
import com.xinkao.erp.scene.query.SceneQuery;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzl
 * @since 2025-04-20 16:23:50
 */
@Mapper
public interface ScenePcdImgMapper extends BaseMapper<ScenePcdImg> {

    Page<ScenePcdImg> page(Page page, ScenePcdImgQuery query);
}
