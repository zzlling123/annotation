package com.xinkao.erp.common.model;

import java.util.List;
import com.xinkao.erp.user.entity.Role;
import com.xinkao.erp.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 登录用户身份权限
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class LoginUser  {

    private static final long serialVersionUID = 1L;

    /**
     * 登录token
     */
    private String token;
    /**
     * 登录时间：yyyy-MM-dd HH:mm:ss
     */
    private Long loginTs;
    /**
     * 过期时间：yyyy-MM-dd HH:mm:ss
     */
    private Long expireTime;
    /**
     * 登录IP地址
     */
    private String ipAddr;
    /**
     * 登录地点
     */
    private String loginLocation;
    /**
     * 浏览器类型
     */
    private String browser;
    /**
     * 操作系统
     */
    private String os;
    /**
     * 用户信息
     */
    private User user;
    /**
     * 角色列表
     */
    private List<Role> roleList;

    public LoginUser(User user) {
        this.user = user;
    }

}
