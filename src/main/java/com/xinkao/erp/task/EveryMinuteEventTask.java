package com.xinkao.erp.task;

import javax.annotation.Resource;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.common.validation.constraint.Date;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.exam.service.ExamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class EveryMinuteEventTask {

    @Resource
    private ExamService examService;

}
