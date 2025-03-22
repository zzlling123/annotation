package com.xinkao.erp.manage.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.manage.query.ClassInfoQuery;
import com.xinkao.erp.manage.vo.ClassInfoVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 班级表 Mapper 接口
 * </p>
 *
 * @author Ldy
 * @since 2025-03-21 14:32:24
 */
@Mapper
public interface ClassInfoMapper extends BaseMapper<ClassInfo> {

    Page<ClassInfoVo> page(Page page,ClassInfoQuery query);
}
