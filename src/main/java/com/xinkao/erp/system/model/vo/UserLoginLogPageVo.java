package com.xinkao.erp.system.model.vo;

import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.login.entity.UserLoginLog;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 登录日志查询分页返回实体
 **/
@Setter
@Getter
@ApiModel("登录日志查询实体-VO")
public class UserLoginLogPageVo implements OutputConverter<UserLoginLogPageVo, UserLoginLog> {

    @ApiModelProperty("日志ID")
    private String id;

    @ApiModelProperty("登录账号")
    private String account;

    @ApiModelProperty("登录人员")
    private String realName;

    @ApiModelProperty("浏览器类型")
    private String browser;

    @ApiModelProperty("登录IP")
    private String ipAddr;

    @ApiModelProperty("登录时间")
    private String loginTime;

    @ApiModelProperty("登陆地点")
    private String loginLocation;

    @ApiModelProperty("登录系统")
    private String os;

    @ApiModelProperty("登录状态")
    private String status;


}
