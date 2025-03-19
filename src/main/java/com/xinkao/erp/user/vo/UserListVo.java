package com.xinkao.erp.user.vo;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.user.entity.User;

import lombok.Data;
/**
 * 用户列表
 * @author hys_thanks
 *
 */
@Data
public class UserListVo extends UserInfoComVo implements OutputConverter<UserListVo, User>{
    /** 账号主键 **/
	private String userId;
    /**
     * 角色列表
     */
    private List<RoleVo> roleList;
    /**
     * 是否锁定:0-未锁定 1-锁定
     */
    private Integer isLocked;
    /**
     * 锁定日期
     */
    private String lockTime;
    /**
     * 锁定原因
     */
    private String lockCause;
    /**
     * 最近一次登录ip
     */
    private String lastLoginIp;
    /**
     * 最近一次登录时间
     */
    @TableField("last_login_time")
    private String lastLoginTime;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
}
