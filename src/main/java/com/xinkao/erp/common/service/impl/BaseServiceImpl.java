package com.xinkao.erp.common.service.impl;



import javax.annotation.Resource;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinkao.erp.common.exception.NotFoundException;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.common.model.entity.BaseEntity;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.common.util.RedisUtil;

import com.xinkao.erp.login.service.UserOptLogService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T>
    implements BaseService<T> {
	
	@Resource
	protected RedisUtil redisUtil;


    public static void assertNotNull(Object data) {
        assertNotNull(data, "数据未找到");
    }

    public static void assertNotNull(Object data, String message) {
        if (null == data) {
            throw new NotFoundException(message);
        }
    }

}
