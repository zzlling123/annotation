package com.xinkao.erp.common.exception;

import com.xinkao.erp.common.enums.system.XinKaoState;

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
