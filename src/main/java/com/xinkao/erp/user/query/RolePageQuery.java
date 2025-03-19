package com.xinkao.erp.user.query;

import com.xinkao.erp.common.model.BasePageQuery;

import lombok.Getter;
import lombok.Setter;

/**
 * 管理端角色查询
 * @author hys_thanks
 */
@Setter
@Getter
public class RolePageQuery extends BasePageQuery {

    /**角色名称**/
    private String roleName;

}