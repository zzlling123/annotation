package com.xinkao.erp.common.exception;

import com.xinkao.erp.common.enums.system.XinKaoState;

/**
 * 权限不足异常
 * @ClassName AuthenticationException
 * @Description
 * @Author 777
 * @Date 2021/10/14 10:09
 **/
public class RoleErrorException extends AbstractXinKaoException {

    public RoleErrorException(String message) {
        super(message);
    }

    public RoleErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public XinKaoState getState() {
        return XinKaoState.FAIL;
    }
}
