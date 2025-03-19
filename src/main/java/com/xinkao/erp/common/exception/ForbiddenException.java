package com.xinkao.erp.common.exception;

/**
 * 禁止访问异常
 **/
public class ForbiddenException extends AbstractXinKaoException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

}
