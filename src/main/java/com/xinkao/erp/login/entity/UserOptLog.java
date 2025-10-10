package com.xinkao.erp.login.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_user_opt_log")
public class UserOptLog extends BaseEntity {



    @TableId(type = IdType.AUTO)
    private Integer id;



    @TableField("date_str")
    private String dateStr;




    @TableField("content")
    private String content;




    @TableField("account")
    private String account;




    @TableField("user_id")
    private String userId;




    @TableField("real_name")
    private String realName;




    @TableField("operation_type")
    private String operationType;




    @TableField("status")
    private Integer status;




    @TableField("client_ip")
    private String clientIp;




    @TableField("ip_region")
    private String ipRegion;




    @TableField("method")
    private String method;




    @TableField("request_method")
    private String requestMethod;




    @TableField("request_url")
    private String requestUrl;




    @TableField("request_param")
    private String requestParam;




    @TableField("request_time")
    private String requestTime;




    @TableField("cost_time")
    private Long costTime;




    @TableField("browser")
    private String browser;




    @TableField("os")
    private String os;




    @TableField("user_agent")
    private String userAgent;




    @TableField("response_data")
    private String responseData;




    @TableField("error_msg")
    private String errorMsg;


}
