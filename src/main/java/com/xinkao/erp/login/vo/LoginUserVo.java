package com.xinkao.erp.login.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.vo.MenuVo;
import com.xinkao.erp.user.vo.RoleVo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录用户基本信息-vo
 * @author hys_thanks
 *
 */
@Data
public class LoginUserVo implements OutputConverter<LoginUserVo, User>{

    @ApiModelProperty("用户登录标识")
    private String token;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("性别")
    private Integer sex;

    @ApiModelProperty("姓名")
    private String realName;

    @ApiModelProperty("角色ID")
    private Integer roleId;

    @ApiModelProperty("是否启用0否1是")
    private Integer state;
}
