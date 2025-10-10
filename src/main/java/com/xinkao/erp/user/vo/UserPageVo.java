package com.xinkao.erp.user.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class UserPageVo extends BaseEntity {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("姓名")
    private String realName;

    @ApiModelProperty("是否启用0否1是")
    private Integer state;

    @ApiModelProperty("角色ID")
    private Integer roleId;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("班级ID")
    private Integer classId;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("身份证号")
    private String idCard;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("职务")
    private String duty;

    @ApiModelProperty("性别")
    private String sex;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
}
