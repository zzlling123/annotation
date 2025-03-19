package com.xinkao.erp.system.model.vo;

import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.login.entity.UserOptLog;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel("操作日志详情查询实体-VO")
public class UserOptLogDetailsVo implements OutputConverter<UserOptLogDetailsVo, UserOptLog> {

    @ApiModelProperty("日志ID")
    private String id;

    @ApiModelProperty("操作账号")
    private String account;

    @ApiModelProperty("操作人员")
    private String realName;

    @ApiModelProperty("操作内容")
    private String content;

    @ApiModelProperty("操作状态")
    private String status;

    @ApiModelProperty("操作时间")
    private String requestTime;

    @ApiModelProperty("请求方式")
    private String requestMethod;

    @ApiModelProperty("请求参数")
    private String requestParam;

    @ApiModelProperty("操作系统")
    private String os;

    @ApiModelProperty("操作方法")
    private String method;

    @ApiModelProperty("操作地点 ip所属区域")
    private String ipRegion;

    @ApiModelProperty("错误信息")
    private String errorMsg;

    @ApiModelProperty("操作耗时")
    private Long costTime;

    @ApiModelProperty("客户端IP")
    private String clientIp;

    @ApiModelProperty("请求路径")
    private String requestUrl;

    @ApiModelProperty("返回数据")
    private String responseData;


}
