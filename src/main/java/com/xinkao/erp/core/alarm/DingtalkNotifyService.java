package com.xinkao.erp.core.alarm;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.xinkao.erp.core.alarm.dto.DingtalkAlarmContentRequest;
import com.xinkao.erp.core.alarm.dto.DingtalkAlarmRequest;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DingtalkNotifyService {
	
    @Value("${dingtalk.alarm.enabled}")
    private Boolean alarmEnabled;

    @Value("${dingtalk.alarm.url}")
    private String alarmUrl;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public void alarm(DingtalkAlarmRequest request) {
        if (Boolean.TRUE.equals(alarmEnabled)) {
            threadPoolTaskExecutor.submit(() -> {
                String requestJson = JSON.toJSONString(request);
                log.error("钉钉异常告警,request={}", requestJson);
                String responseJson = HttpUtil.createPost(alarmUrl)
                        .header("Content-Type", "application/json; charset=utf-8")
                        .body(requestJson)
                        .execute().body();
                log.error("钉钉业务告警,response={}", responseJson);
            });
        }
    }
    
    public void alarm(String errorMessage) {
    	   DingtalkAlarmContentRequest alarmContentRequest = DingtalkAlarmContentRequest.builder().build();
           alarmContentRequest.buildContent("[校园师训]"+errorMessage);
           DingtalkAlarmRequest alarmRequest = DingtalkAlarmRequest.builder()
                   .text(alarmContentRequest)
                   .build();
           alarm(alarmRequest);
    }
}
