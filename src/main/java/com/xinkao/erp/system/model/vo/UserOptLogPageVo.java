package com.xinkao.erp.system.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.login.entity.UserOptLog;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel("操作日志查询实体-VO")
public class UserOptLogPageVo implements OutputConverter<UserOptLogPageVo, UserOptLog> {

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

    @ApiModelProperty("客户端IP")
    private String clientIp;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("操作类型")
    private String operationType;


}
