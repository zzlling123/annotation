package com.xinkao.erp.manage.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.manage.query.ClassInfoQuery;
import com.xinkao.erp.manage.vo.ClassInfoVo;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ClassInfoMapper extends BaseMapper<ClassInfo> {

    Page<ClassInfoVo> page(Page page,ClassInfoQuery query);
}
