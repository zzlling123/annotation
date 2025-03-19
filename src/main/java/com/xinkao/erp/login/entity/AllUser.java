package com.xinkao.erp.login.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 总账号表
 * </p>
 *
 * @author Ldy
 * @since 2023-12-14 21:34:43
 */
@Getter
@Setter
@TableName("all_user")
public class AllUser extends DataEntity {

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("salt")
    private String salt;

    @TableField("sex")
    private Integer sex;

    @TableField("mobile")
    private String mobile;

    @TableField("real_name")
    private String realName;

    @TableField("role_id")
    private Integer roleId;

    @TableField("enterprise_id")
    private Integer enterpriseId;

    @TableField("state")
    private Integer state;

    @TableField("is_del")
    private Integer isDel;

    @TableField("is_operate")
    private Integer isOperate;

    @TableField(exist = false)
    private Integer isSuper;

    @TableField(exist = false)
    private Integer departmentId;

}
