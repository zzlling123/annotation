package com.xinkao.erp.common.exception;

public class ForbiddenException extends AbstractXinKaoException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

}
