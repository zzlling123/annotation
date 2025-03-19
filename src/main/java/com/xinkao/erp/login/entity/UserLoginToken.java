package com.xinkao.erp.login.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 管理端-登录token(定时删除)
 * </p>
 *
 * @author hanhys
 * @since 2023-08-03 14:18:36
 */
@Getter
@Setter
@TableName("sys_user_login_token")
public class UserLoginToken extends BaseEntity {

    /**
     * token作为主键
     */
    @TableId("token")
    private String token;

    /**
     * 用户主键
     */
    @TableField("user_id")
    private String userId;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private String startTime;

    /**
     * 上次处理时间
     */
    @TableField("last_time")
    private String lastTime;


}
