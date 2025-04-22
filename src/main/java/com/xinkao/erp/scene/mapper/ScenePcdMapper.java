package com.xinkao.erp.scene.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.scene.entity.Scene;
import com.xinkao.erp.scene.entity.ScenePcd;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.scene.query.ScenePcdQuery;
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
public interface ScenePcdMapper extends BaseMapper<ScenePcd> {

    Page<ScenePcd> page(Page page, ScenePcdQuery query);
}
