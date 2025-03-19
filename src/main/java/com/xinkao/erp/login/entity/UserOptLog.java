package com.xinkao.erp.login.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 管理端-操作记录(分表)
 * </p>
 *
 * @author hanhys
 * @since 2023-08-03 14:18:36
 */
@Getter
@Setter
@TableName("sys_user_opt_log")
public class UserOptLog extends BaseEntity {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 归档日期:yyyy-MM-dd
     */
    @TableField("date_str")
    private String dateStr;

    /**
     * 操作内容
     */
    @TableField("content")
    private String content;

    /**
     * 用户登录账号
     */
    @TableField("account")
    private String account;

    /**
     * 账户主键
     */
    @TableField("user_id")
    private String userId;

    /**
     * 用户姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 操作类型
     */
    @TableField("operation_type")
    private String operationType;

    /**
     * 操作状态（0：正常，1：异常）
     */
    @TableField("status")
    private Integer status;

    /**
     * 客户端IP
     */
    @TableField("client_ip")
    private String clientIp;

    /**
     * ip所属地区
     */
    @TableField("ip_region")
    private String ipRegion;

    /**
     * 请求的方法
     */
    @TableField("method")
    private String method;

    /**
     * 请求方式
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 请求路径
     */
    @TableField("request_url")
    private String requestUrl;

    /**
     * 请求参数
     */
    @TableField("request_param")
    private String requestParam;

    /**
     * 请求时间
     */
    @TableField("request_time")
    private String requestTime;

    /**
     * 请求耗时（毫秒单位）
     */
    @TableField("cost_time")
    private Long costTime;

    /**
     * 浏览器
     */
    @TableField("browser")
    private String browser;

    /**
     * 操作系统
     */
    @TableField("os")
    private String os;

    /**
     * 访问的ua
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 返回数据
     */
    @TableField("response_data")
    private String responseData;

    /**
     * 错误提示信息
     */
    @TableField("error_msg")
    private String errorMsg;


}
