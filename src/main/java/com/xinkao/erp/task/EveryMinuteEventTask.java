package com.xinkao.erp.task;

import javax.annotation.Resource;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.exam.service.ExamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 分钟级事件处理事件发送
 * 每分钟发送事件,接收事件后,如果事务未处理完成,跳过即可
 **/
@Slf4j
@Component
public class EveryMinuteEventTask {

    @Resource
    private ExamService examService;

    //    @Scheduled(cron = "0 * * * * ?")
    public void updateExamStatus() {
        log.info("更新考试项目,考试状态的状态...开始");
        // 获取未开始的考试（state小于等于10）且开始时间大于当前时间的考试
        List<Exam> examList = examService.lambdaQuery()
                .le(Exam::getState, 10)
                .ge(Exam::getStartTime, LocalDateTime.now())
                .list();
        if (examList.size() > 0) {
            for (Exam exam : examList) {
                exam.setState(20);
                examService.updateById(exam);
            }
        }
        log.info("更新考试项目,考试状态的状态...结束");
    }

}
