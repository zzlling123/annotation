package com.xinkao.erp.common.exception;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.xinkao.erp.common.enums.system.XinKaoState;

/**
 * 项目的抽象异常基类
 **/
public abstract class AbstractXinKaoException extends RuntimeException {

    private Object errorData;

    public AbstractXinKaoException(String message) {
        super(message);
    }

    public AbstractXinKaoException(String message, Throwable cause) {
        super(message, cause);
    }

    @NonNull
    public XinKaoState getState() {
        return XinKaoState.FAIL;
    }

    @Nullable
    public Object getErrorData() {
        return errorData;
    }

    @NonNull
    public AbstractXinKaoException setErrorData(@Nullable Object errorData) {
        this.errorData = errorData;
        return this;
    }
}
