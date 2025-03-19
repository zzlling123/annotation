package com.xinkao.erp.common.exception;

import com.xinkao.erp.common.enums.system.XinKaoState;

/**
 * 认证异常
 **/
public class AuthenticationException extends AbstractXinKaoException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public XinKaoState getState() {
        return XinKaoState.UNAUTHORIZED;
    }
}
