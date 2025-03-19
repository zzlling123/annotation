package com.xinkao.erp.user.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * </p>
 *
 * @author Ldy
 * @since 2023-05-09 14:02:05
 */
@Getter
@Setter
public class UserPageVo extends BaseEntity {

    @ApiModelProperty("用户ID")
    private Integer id;

    @ApiModelProperty("姓名")
    private String realName;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("职务")
    private String dutie;

    @ApiModelProperty("角色ID")
    private Integer roleId;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("是否启用0否1是")
    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
}
