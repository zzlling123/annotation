package com.xinkao.erp.login.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_user_login_token")
public class UserLoginToken extends BaseEntity {

    @TableId("token")
    private String token;

    @TableField("user_id")
    private String userId;

    @TableField("start_time")
    private String startTime;

    @TableField("last_time")
    private String lastTime;


}
