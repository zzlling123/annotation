package com.xinkao.erp.login.param;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

/**
 * 账号密码登录参数
 **/
@Data
public class ApLoginParam {

    /**
     * 钉钉code
     */
    private String code;
}
