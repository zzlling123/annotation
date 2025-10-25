package com.xinkao.erp.common.exception.file;

import com.xinkao.erp.common.exception.AbstractXinKaoException;


public class FileException extends AbstractXinKaoException {
    private static final long serialVersionUID = 1L;

    public FileException(String message) {
        super(message);
    }

    public FileException(String message, Throwable cause) {
        super(message, cause);
    }

}
