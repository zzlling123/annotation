package com.xinkao.erp.common.exception;

/**
 * 和业务相关的异常
 **/
public class BusinessException extends AbstractXinKaoException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}
