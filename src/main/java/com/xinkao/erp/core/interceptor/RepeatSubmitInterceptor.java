package com.xinkao.erp.core.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.common.annotation.RepeatSubmit;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.util.ServletUtils;
@Component
public abstract class RepeatSubmitInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if (annotation != null) {
                if (this.isRepeatSubmit(request)) {
                    BaseResponse baseResponse = BaseResponse.fail("不允许重复提交，请稍后再试");
                    ServletUtils.renderString(response, JSONObject.toJSONString(baseResponse));
                    return false;
                }
            }
            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
    }

    public abstract boolean isRepeatSubmit(HttpServletRequest request);
}
