package com.xinkao.erp.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 账号表
 * </p>
 *
 * @author Ldy
 * @since 2023-03-02 09:42:04
 */
@Getter
@Setter
@TableName("user")
@ApiModel(value = "User对象", description = "账号表")
public class User extends DataEntity {

    @ApiModelProperty("手机号")
    @TableField("mobile")
    private String mobile;

    @ApiModelProperty("姓名")
    @TableField("real_name")
    private String realName;

    @ApiModelProperty("钉钉ID")
    @TableField("ding_id")
    private String dingId;

    @ApiModelProperty("职务")
    @TableField("dutie")
    private String dutie;

    @ApiModelProperty("是否删除0否1是")
    @TableField("is_del")
    private Integer isDel;

    @ApiModelProperty("是否启用0否1是")
    @TableField("state")
    private Integer state;

    @ApiModelProperty("角色ID")
    @TableField("role_id")
    private Integer roleId;

    @ApiModelProperty("ip")
    @TableField(exist = false)
    private String ip;

    @TableField(exist = false)
    private Integer level;



}
