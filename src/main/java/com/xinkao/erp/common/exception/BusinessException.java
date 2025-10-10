package com.xinkao.erp.common.exception;


public class BusinessException extends AbstractXinKaoException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}
