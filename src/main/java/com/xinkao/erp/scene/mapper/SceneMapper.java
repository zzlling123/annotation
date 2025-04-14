package com.xinkao.erp.scene.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.scene.entity.Scene;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.scene.query.SceneQuery;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzl
 * @since 2025-04-12 23:11:56
 */
@Mapper
public interface SceneMapper extends BaseMapper<Scene> {

    Page<Scene> page(Page page, SceneQuery query);
}
