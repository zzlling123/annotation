package com.xinkao.erp.common.model;

import java.util.List;
import com.xinkao.erp.user.entity.Role;
import com.xinkao.erp.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class LoginUser  {

    private static final long serialVersionUID = 1L;

    private String token;
    private Long loginTs;
    
    private Long expireTime;
    
    private String ipAddr;
    
    private String loginLocation;
    
    private String browser;
    
    private String os;
    
    private User user;
    
    private List<Role> roleList;

    public LoginUser(User user) {
        this.user = user;
    }

}
