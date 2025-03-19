package com.xinkao.erp.common.exception;

/**
 * 数据未找到异常
 **/
public class NotFoundException extends AbstractXinKaoException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
