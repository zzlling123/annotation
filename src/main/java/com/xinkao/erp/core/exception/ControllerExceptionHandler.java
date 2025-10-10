package com.xinkao.erp.core.exception;

import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.xinkao.erp.common.enums.system.XinKaoState;
import com.xinkao.erp.common.exception.AbstractXinKaoException;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.util.ValidationUtil;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public BaseResponse<?> handleDataIntegrityViolationException(
        DataIntegrityViolationException e) {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setState(XinKaoState.FAIL.value());
        baseResponse.setMsg("字段验证错误，请完善后重试！");
        return baseResponse;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public BaseResponse<?> handleMissingServletRequestParameterException(
        MissingServletRequestParameterException e) {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setState(XinKaoState.FAIL.value());
        baseResponse.setMsg(
            String.format("请求字段缺失, 类型为 %s，名称为 %s", e.getParameterType(), e.getParameterName()));
        return baseResponse;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse<?> handleConstraintViolationException(ConstraintViolationException e) {
        BaseResponse<Map<String, String>> baseResponse = handleBaseException(e);
        baseResponse.setMsg(ValidationUtil.strWithValidError(e.getConstraintViolations()));
        baseResponse.setState(XinKaoState.FAIL.value());
        baseResponse.setData(ValidationUtil.mapWithValidError(e.getConstraintViolations()));
        return baseResponse;
    }

    @ExceptionHandler(BindException.class)
    public BaseResponse<?> handleBindViolationException(BindException e) {
        BaseResponse<Map<String, String>> baseResponse = handleBaseException(e);
        baseResponse.setState(XinKaoState.FAIL.value());
        baseResponse.setMsg(ValidationUtil.strWithFieldError(e.getBindingResult().getFieldErrors()));
        baseResponse.setData(ValidationUtil.mapWithFieldError(e.getBindingResult().getFieldErrors()));
        return baseResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        BaseResponse<Map<String, String>> baseResponse = handleBaseException(e);
        baseResponse.setState(XinKaoState.FAIL.value());
        baseResponse.setMsg(ValidationUtil.strWithFieldError(e.getBindingResult().getFieldErrors()));
        baseResponse.setData(ValidationUtil.mapWithFieldError(e.getBindingResult().getFieldErrors()));
        return baseResponse;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public BaseResponse<?> handleIllegalArgumentException(
        IllegalArgumentException e) {
        BaseResponse<Map<String, String>> baseResponse = handleBaseException(e);
        baseResponse.setState(XinKaoState.FAIL.value());
        baseResponse.setMsg(e.getMessage());
        return baseResponse;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public BaseResponse<?> handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException e) {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setState(XinKaoState.FAIL.value());
        baseResponse.setMsg(String.format("请求方式%s不支持", e.getMethod()));
        return baseResponse;
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public BaseResponse<?> handleHttpMediaTypeNotAcceptableException(
        HttpMediaTypeNotAcceptableException e) {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setState(XinKaoState.FAIL.value());
        baseResponse.setMsg("不支持的期望响应媒体类型");
        return baseResponse;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public BaseResponse<?> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException e) {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setState(XinKaoState.FAIL.value());
        baseResponse.setMsg("缺失请求主体");
        return baseResponse;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public BaseResponse<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setState(XinKaoState.FAIL.value());
        baseResponse.setMsg("错误的路由");
        return baseResponse;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public BaseResponse<?> handleUploadSizeExceededException(MaxUploadSizeExceededException e) {
        BaseResponse<Object> baseResponse = handleBaseException(e);
        baseResponse.setState(XinKaoState.FAIL.value());
        baseResponse.setMsg("当前请求超出最大限制：" + e.getMaxUploadSize() + " bytes");
        return baseResponse;
    }
    
    @ExceptionHandler(AbstractXinKaoException.class)
    public BaseResponse<?> handleHaloException(AbstractXinKaoException e) {
        BaseResponse<Object> baseResponse = handleBaseException(e);
        baseResponse.setState(e.getState().value());
        baseResponse.setData(e.getErrorData());
        return baseResponse;
    }
    
    @ExceptionHandler(Exception.class)
    public BaseResponse<?> handleGlobalException(Exception e) {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setState(XinKaoState.FAIL.value());
        baseResponse.setMsg("内部服务异常");
        return baseResponse;
    }

    private <T> BaseResponse<T> handleBaseException(Throwable t) {
        Assert.notNull(t, "Throwable must not be null");

        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setMsg(t.getMessage());
        log.error("Captured an exception:", t);
        if (log.isDebugEnabled()) {
            baseResponse.setDevMsg(ExceptionUtil.stacktraceToString(t));
        }

        return baseResponse;
    }
}
