package com.xinkao.erp.core.interceptor.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.common.constant.XinKaoConstant;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.core.filter.RepeatedlyRequestWrapper;
import com.xinkao.erp.core.interceptor.RepeatSubmitInterceptor;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class SameUrlDataIntercept extends RepeatSubmitInterceptor {

    public final String REPEAT_PARAMS = "repeatParams";

    public final String REPEAT_TIME = "repeatTime";

    @Resource
    private RedisUtil redisUtil;

    
    private int intervalTime = 3;

    @Override
    public boolean isRepeatSubmit(HttpServletRequest request) {
        String nowParams = "";
        if (request instanceof RepeatedlyRequestWrapper) {
            RepeatedlyRequestWrapper repeatedlyRequest = (RepeatedlyRequestWrapper) request;
            try {
                nowParams = IoUtil.readUtf8(repeatedlyRequest.getInputStream());
            } catch (IOException e) {
                log.warn("读取流出现问题！");
            }
        }

        if (StrUtil.isNotEmpty(nowParams)) {
            nowParams = JSONObject.toJSONString(request.getParameterMap());
        }
        Map<String, Object> nowDataMap = new HashMap();
        nowDataMap.put(REPEAT_PARAMS, nowParams);
        nowDataMap.put(REPEAT_TIME, System.currentTimeMillis());

        String url = request.getRequestURI();

        String submitKey = request.getHeader(XinKaoConstant.ACCESS_TOKEN);
        if (Validator.isEmpty(submitKey)) {
            submitKey = url;
        }

        String cacheRepeatKey = XinKaoConstant.REPEAT_SUBMIT_KEY + submitKey;

        Object sessionObj = redisUtil.getMap(cacheRepeatKey);
        if (sessionObj != null) {
            Map<String, Object> sessionMap = (Map<String, Object>) sessionObj;
            if (sessionMap.containsKey(url)) {
                Map<String, Object> preDataMap = (Map<String, Object>) sessionMap.get(url);
                if (compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap)) {
                    return true;
                }
            }
        }
        Map<String, Object> cacheMap = new HashMap();
        cacheMap.put(url, nowDataMap);
        redisUtil.setMap(cacheRepeatKey, cacheMap, intervalTime, TimeUnit.SECONDS);
        return false;
    }

    
    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
        String nowParams = (String) nowMap.get(REPEAT_PARAMS);
        String preParams = (String) preMap.get(REPEAT_PARAMS);
        return nowParams.equals(preParams);
    }

    
    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap) {
        long time1 = (Long) nowMap.get(REPEAT_TIME);
        long time2 = (Long) preMap.get(REPEAT_TIME);
        return (time1 - time2) < (this.intervalTime * 1000L);
    }
}
