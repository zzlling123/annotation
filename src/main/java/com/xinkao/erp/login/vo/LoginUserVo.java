package com.xinkao.erp.login.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.vo.MenuVo;
import com.xinkao.erp.user.vo.RoleVo;

import lombok.Data;

/**
 * 登录用户基本信息-vo
 * @author hys_thanks
 *
 */
@Data
public class LoginUserVo implements OutputConverter<LoginUserVo, User>{
    
    @JsonIgnore
    /**用户标识**/
    private String id;

    private String userId;

    private String mobile;

    private String realName;

    private String dutie;

    private String dingId;

    private Integer state;

    private Integer roleId;

	
}
