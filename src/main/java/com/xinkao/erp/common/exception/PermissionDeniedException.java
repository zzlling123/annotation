package com.xinkao.erp.common.exception;

import com.xinkao.erp.common.enums.system.XinKaoState;

public class PermissionDeniedException extends AbstractXinKaoException {

    public PermissionDeniedException(String message) {
        super(message);
    }

    public PermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public XinKaoState getState() {
        return XinKaoState.DENIED;
    }
}
