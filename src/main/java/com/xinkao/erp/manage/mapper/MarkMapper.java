package com.xinkao.erp.manage.mapper;

import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.manage.query.MarkQuery;
import com.xinkao.erp.user.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface MarkMapper extends BaseMapper<Mark> {

    List<Mark> getMarkList(@Param("query") MarkQuery query);
}
