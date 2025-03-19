package com.xinkao.erp.core.advice;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.xinkao.erp.common.enums.system.XinKaoState;
import com.xinkao.erp.common.model.BaseResponse;

/**
 * 封装返回的结果
 **/
@ControllerAdvice("com.xinkao.erp")
public class CommonResultControllerAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
        Class<? extends HttpMessageConverter<?>> converterType) {
        return AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType contentType,
        Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request,
        ServerHttpResponse response) {
        MappingJacksonValue container = getOrCreateContainer(body);
        // The contain body will never be null
        beforeBodyWriteInternal(container, contentType, returnType, request, response);
        return container;
    }

    /**
     * Wrap the body in a {@link MappingJacksonValue} value container (for providing
     * additional serialization instructions) or simply cast it if already wrapped.
     */
    private MappingJacksonValue getOrCreateContainer(Object body) {
        return body instanceof MappingJacksonValue ? (MappingJacksonValue) body :
            new MappingJacksonValue(body);
    }

    private void beforeBodyWriteInternal(MappingJacksonValue bodyContainer,
        MediaType contentType,
        MethodParameter returnType,
        ServerHttpRequest request,
        ServerHttpResponse response) {
        // 获取返回对象
        Object returnBody = bodyContainer.getValue();

        if (returnBody instanceof BaseResponse) {
            // 如果返回的是BaseResponse不做处理
            return;
        }
        // 将HttpStatus转为XinKaoState
        XinKaoState state = XinKaoState.OK;
        if (response instanceof ServletServerHttpResponse) {
            HttpServletResponse servletResponse =
                ((ServletServerHttpResponse) response).getServletResponse();
            HttpStatus status = HttpStatus.resolve(servletResponse.getStatus());
            if (status == null || status.isError()) {
                state = XinKaoState.FAIL;
            }
        }
        BaseResponse baseResponse = new BaseResponse(state.value(), state.getReasonPhrase(), returnBody);
        bodyContainer.setValue(baseResponse);
    }
}
