package com.xinkao.erp.task.history;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xinkao.erp.common.util.mapper.BeanMapper;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 学生归档历史表
 * @author hys_thanks
 *
 */
@Slf4j
@Component
public class StudentToHistoryTask {

	    
    /**
     * 学生信息归档,每年3月份执行
     */
//    @Scheduled(cron = "00 00 09 * 03 ?")
    public void studentToHistoryTask() {
    	log.error("学生归档历史表....开始");
    	int year = DateUtil.date().getField(DateField.YEAR);
    	Integer lastYear = year - 3;
    	Integer pageSize = 5000;
//    	Long count = examStudentService.lambdaQuery().eq(ExamStudent::getYear,lastYear).count();
//    	Integer page = count.intValue() /pageSize;//总页数
//    	if(count.intValue()%pageSize != 0) {
//    		page = page + 1;
//    	}
//    	for(int i = 0; i< page; i++) {
//    		List<ExamStudent> studentList = examStudentService.lambdaQuery().eq(ExamStudent::getYear,lastYear)
//    								.orderByAsc(ExamStudent::getId)
//									.last("limit "+pageSize).list();
//    		List<ExamStudentHis> studentHisList = studentList.stream().map(st->{
//    			return BeanMapper.map(st, ExamStudentHis.class);
//    		}).collect(Collectors.toList());
//    		examStudentHisService.saveBatch(studentHisList, 500);
//    	}
    	log.error("学生归档历史表....结束");
    }
}
