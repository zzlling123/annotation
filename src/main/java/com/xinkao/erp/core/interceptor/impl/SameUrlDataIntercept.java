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

/**
 * 判断请求url和数据是否和上一次相同，如果和上次相同，则是重复提交表单
 **/
@Component
@Slf4j
public class SameUrlDataIntercept extends RepeatSubmitInterceptor {

    public final String REPEAT_PARAMS = "repeatParams";

    public final String REPEAT_TIME = "repeatTime";

    @Resource
    private RedisUtil redisUtil;

    /**
     * 间隔时间，单位:秒 默认3秒
     * 两次相同参数的请求，如果间隔时间大于该参数，系统不会认定为重复提交的数据
     */
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

        // body参数为空，获取Parameter的数据
        if (StrUtil.isNotEmpty(nowParams)) {
            nowParams = JSONObject.toJSONString(request.getParameterMap());
        }
        Map<String, Object> nowDataMap = new HashMap();
        nowDataMap.put(REPEAT_PARAMS, nowParams);
        nowDataMap.put(REPEAT_TIME, System.currentTimeMillis());

        // 请求地址（作为存放cache的key值）
        String url = request.getRequestURI();

        // 唯一值（没有消息头则使用请求地址）
        String submitKey = request.getHeader(XinKaoConstant.ACCESS_TOKEN);
        if (Validator.isEmpty(submitKey)) {
            submitKey = url;
        }
        // 唯一标识（指定key + 消息头）
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

    /**
     * 判断参数是否相同
     * @param nowMap 本次参数
     * @param preMap 上次参数
     * @return boolean 是否相同
     */
    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
        String nowParams = (String) nowMap.get(REPEAT_PARAMS);
        String preParams = (String) preMap.get(REPEAT_PARAMS);
        return nowParams.equals(preParams);
    }

    /**
     * 判断两次间隔时间
     * @param nowMap 本次时间
     * @param preMap 上次时间
     * @return 注释
     */
    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap) {
        long time1 = (Long) nowMap.get(REPEAT_TIME);
        long time2 = (Long) preMap.get(REPEAT_TIME);
        return (time1 - time2) < (this.intervalTime * 1000L);
    }
}
