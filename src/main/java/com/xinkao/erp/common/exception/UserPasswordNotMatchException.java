package com.xinkao.erp.common.exception;

import com.xinkao.erp.common.enums.system.XinKaoState;

public class UserPasswordNotMatchException extends AbstractXinKaoException {

    public UserPasswordNotMatchException(String message) {
        super(message);
    }

    public UserPasswordNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public XinKaoState getState() {
        return XinKaoState.UNAUTHORIZED;
    }
}
