package com.xinkao.erp.task.examplan;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;

/**
 * 考试计划生成策略:
 * 每天0点生成一次
 * @author hys_thanks
 */
@Slf4j
@Component
public class ExamPlanGenTask {

	    
    /**
     * 考试计划校验生成
     */
//    @Scheduled(cron = "01 00 00 * * ?")
    public void endCourse() {
//    	log.error("考试计划校验生成");
//    	examPlanService.genExamPlan();
    }
}
