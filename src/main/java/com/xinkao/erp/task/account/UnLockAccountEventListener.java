package com.xinkao.erp.task.account;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.xinkao.erp.common.event.EverySecondEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UnLockAccountEventListener implements ApplicationListener<EverySecondEvent>{

    @Override
    public void onApplicationEvent(EverySecondEvent event) {

    }
}
