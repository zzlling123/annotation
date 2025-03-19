package com.xinkao.erp.core.mybatisplus.handler;

import java.util.Date;

import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.util.RedisUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.xinkao.erp.common.model.entity.DataEntity;
import com.xinkao.erp.user.entity.User;

import javax.annotation.Resource;

/**
 * 自定义元对象处理器
 **/
@Component
public class CustomMetaObjectHandler implements MetaObjectHandler {

    @Resource
    protected RedisUtil redisUtil;

    @Override
    public void insertFill(MetaObject metaObject) {
     // 判断是否有createTime字段，有就自动填充，没有就跳过
        boolean hasCreateTime = metaObject.hasGetter(DataEntity.CREATE_TIME_PROPERTY_NAME);
        boolean hasCreateBy = metaObject.hasGetter(DataEntity.CREATE_BY_PROPERTY_NAME);
        boolean hasUpdateTime = metaObject.hasGetter(DataEntity.UPDATE_TIME_PROPERTY_NAME);
        boolean hasUpdateBy = metaObject.hasGetter(DataEntity.UPDATE_BY_PROPERTY_NAME);
        if (hasCreateTime) {
            if (metaObject.getValue(DataEntity.CREATE_TIME_PROPERTY_NAME) == null) {
                this.setFieldValByName(DataEntity.CREATE_TIME_PROPERTY_NAME, new Date(), metaObject);
            }
        }
        if (hasUpdateTime) {
            if (metaObject.getValue(DataEntity.UPDATE_TIME_PROPERTY_NAME) == null) {
                this.setFieldValByName(DataEntity.UPDATE_TIME_PROPERTY_NAME, new Date(), metaObject);
            }
        }
        String userId = "";
        try {
            LoginUser loginUser = redisUtil.getInfoByToken();
            User curAccount = loginUser.getUser();
            if(curAccount != null) {
                userId = curAccount.getId().toString();
            }
            if (hasCreateBy) {
                if (metaObject.getValue(DataEntity.CREATE_BY_PROPERTY_NAME) == null) {
                    this.setFieldValByName(DataEntity.CREATE_BY_PROPERTY_NAME, userId, metaObject);
                }
            }
            if (hasUpdateBy) {
                if (metaObject.getValue(DataEntity.UPDATE_BY_PROPERTY_NAME) == null) {
                    this.setFieldValByName(DataEntity.UPDATE_BY_PROPERTY_NAME,userId, metaObject);
                }
            }
        } catch (Exception e) {
        	
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        boolean hasUpdateTime = metaObject.hasGetter(DataEntity.UPDATE_TIME_PROPERTY_NAME);
        boolean hasUpdateBy = metaObject.hasGetter(DataEntity.UPDATE_BY_PROPERTY_NAME);
        if (hasUpdateTime) {
            if (metaObject.getValue(DataEntity.UPDATE_TIME_PROPERTY_NAME) == null) {
                this.setFieldValByName(DataEntity.UPDATE_TIME_PROPERTY_NAME, new Date(), metaObject);
            }
        }
        String userId = "";
        try {
            LoginUser loginUser = redisUtil.getInfoByToken();
            User curAccount = loginUser.getUser();
            if(curAccount != null) {
                userId = curAccount.getId().toString();
            }
            if (hasUpdateBy) {
                if (metaObject.getValue(DataEntity.UPDATE_BY_PROPERTY_NAME) == null) {
                    this.setFieldValByName(DataEntity.UPDATE_BY_PROPERTY_NAME, userId, metaObject);
                }
            }
        } catch (Exception e) {
        	
        }
    }
}
