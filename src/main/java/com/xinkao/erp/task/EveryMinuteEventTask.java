package com.xinkao.erp.task;

import javax.annotation.Resource;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 分钟级事件处理事件发送
 * 每分钟发送事件,接收事件后,如果事务未处理完成,跳过即可
 **/
@Component
public class EveryMinuteEventTask {

    @Value("${monitor.APPKEY}")
    private String APPID;
    @Value("${monitor.APPSCERET}")
    private String APPSCERET;
    @Value("${monitor.APPURL}")
    private String APPURL;
    
    @Resource
    private ApplicationEventPublisher eventPublisher;

//    @Scheduled(cron = "0 */10 * * * ?")
    public void AppActive() {
        String appId = APPID;
        String appSecret = APPSCERET;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String newAppSecret = appSecret+"xinkao"+formatter.format(LocalDateTime.now());
        String appSign = SecureUtil.md5(newAppSecret);
        String url = APPURL;
        JSONObject appInfo=new JSONObject();
        appInfo.put("appId",appId);
        appInfo.put("appSign",appSign);
        String result = HttpUtil.post(url,appInfo.toString());
        System.out.println("心跳接收情况："+result);
    }

}
