package com.xinkao.erp.manage.mapper;

import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.manage.query.MarkQuery;
import com.xinkao.erp.user.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 操作题标记类型表 Mapper 接口
 * </p>
 *
 * @author Ldy
 * @since 2025-04-20 21:22:31
 */
@Mapper
public interface MarkMapper extends BaseMapper<Mark> {

    List<Mark> getMarkList(@Param("query") MarkQuery query);
}
