package com.xinkao.erp.common.exception;

import com.xinkao.erp.common.enums.system.XinKaoState;

/**
 * 登录超时异常
 **/
public class AuthenticationExpiredException extends AbstractXinKaoException{

    public AuthenticationExpiredException(String message) {
        super(message);
    }

    public AuthenticationExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public XinKaoState getState() {
        return XinKaoState.EXPIRED;
    }
}
