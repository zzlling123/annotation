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

    @ApiModelProperty("用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty("密码")
    @TableField("password")
    private String password;

    @ApiModelProperty("盐")
    @TableField("salt")
    private String salt;
    //id_card
    //head_img
    //mobile
    //email
    //duty

    @ApiModelProperty("身份证号")
    @TableField("id_card")
    private String idCard;

    @ApiModelProperty("头像")
    @TableField("head_img")
    private String headImg;

    @ApiModelProperty("手机号")
    @TableField("mobile")
    private String mobile;

    @ApiModelProperty("邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty("职务")
    @TableField("duty")
    private String duty;

    @ApiModelProperty("姓名")
    @TableField("real_name")
    private String realName;

    @ApiModelProperty("班级ID")
    @TableField("class_id")
    private Integer classId;

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

    @ApiModelProperty("level")
    @TableField(exist = false)
    private String level;
}
