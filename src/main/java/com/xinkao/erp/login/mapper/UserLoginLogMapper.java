package com.xinkao.erp.login.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.login.entity.UserLoginLog;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.system.model.query.UserLoginLogQuery;
import com.xinkao.erp.system.model.vo.UserLoginLogPageVo;
import org.apache.ibatis.annotations.Param;


public interface UserLoginLogMapper extends BaseMapper<UserLoginLog> {
    Page<UserLoginLogPageVo> page(Page pg , @Param("userLoginLogQuery") UserLoginLogQuery query);
}
