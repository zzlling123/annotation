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

/**
 * Service实现基类
 **/
@Slf4j
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T>
    implements BaseService<T> {
	
	@Resource
	protected RedisUtil redisUtil;


    /**
     * 校验对象是否为null
     * @param data 数据
     */
    public static void assertNotNull(Object data) {
        assertNotNull(data, "数据未找到");
    }

    /**
     * 校验对象是否为null
     * @param data 数据
     * @param message 为空时返回的异常信息
     */
    public static void assertNotNull(Object data, String message) {
        if (null == data) {
            throw new NotFoundException(message);
        }
    }

}
