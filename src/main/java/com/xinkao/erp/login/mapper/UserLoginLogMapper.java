package com.xinkao.erp.login.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.login.entity.UserLoginLog;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.system.model.query.UserLoginLogQuery;
import com.xinkao.erp.system.model.vo.UserLoginLogPageVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 管理端-用户登录表 Mapper 接口
 * </p>
 *
 * @author hanhys
 * @since 2023-08-03 14:18:36
 */
public interface UserLoginLogMapper extends BaseMapper<UserLoginLog> {
    Page<UserLoginLogPageVo> page(Page pg , @Param("userLoginLogQuery") UserLoginLogQuery query);
}
