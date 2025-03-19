package com.xinkao.erp.task;

import javax.annotation.Resource;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xinkao.erp.common.event.EverySecondEvent;

/**
 * 秒级事件处理事件发送
 * 每秒钟发送事件,接收事件后,如果事务未处理完成,跳过即可
 **/
@Component
public class EverySecondEventTask {
    
    @Resource
    private ApplicationEventPublisher eventPublisher;
    
//    @Scheduled(cron = "0/1 * * * * ?")
    public void secondEvent() {
        EverySecondEvent event = new EverySecondEvent(this);
        eventPublisher.publishEvent(event);
    }
}
