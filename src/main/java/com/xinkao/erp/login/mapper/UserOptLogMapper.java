package com.xinkao.erp.login.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.login.entity.UserOptLog;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.system.model.query.UserOptLogQuery;
import com.xinkao.erp.system.model.vo.UserOptLogDetailsVo;
import com.xinkao.erp.system.model.vo.UserOptLogPageVo;
import org.apache.ibatis.annotations.Param;


public interface UserOptLogMapper extends BaseMapper<UserOptLog> {
    Page<UserOptLogPageVo> page(Page pg , @Param("userOptLogQuery") UserOptLogQuery query);

    UserOptLogDetailsVo details(Integer id);
}
