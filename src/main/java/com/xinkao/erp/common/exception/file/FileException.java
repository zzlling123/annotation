package com.xinkao.erp.common.exception.file;

import com.xinkao.erp.common.exception.AbstractXinKaoException;

/**
 * 文件信息异常类
 */
public class FileException extends AbstractXinKaoException {
    private static final long serialVersionUID = 1L;

    public FileException(String message) {
        super(message);
    }

    public FileException(String message, Throwable cause) {
        super(message, cause);
    }

}
