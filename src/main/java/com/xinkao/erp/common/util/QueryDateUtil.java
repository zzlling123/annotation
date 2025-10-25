package com.xinkao.erp.common.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;


public class QueryDateUtil {
	
	public static Integer betweenDay(DateTime startDate,DateTime endDate) {
         long betweenDay = DateUtil.betweenDay(startDate, endDate, true);
         int intValue = Long.valueOf(betweenDay).intValue();
         if(startDate.after(endDate)) {
        	 return -intValue;
         }
         return intValue;
	}
	
	public static Integer betweenMonth(DateTime startDate,DateTime endDate) {
	    long betweenDay = DateUtil.betweenMonth(startDate, endDate, true);
	    int intValue = Long.valueOf(betweenDay).intValue();
	    if(startDate.after(endDate)) {
	        return -intValue;
	    }
	    return intValue;
	}
	
	public static List<String> queryStartDayList(DateTime startDate,String format,int days){
		 List<String> dateList = new ArrayList<String>();
		 dateList.add(startDate.toString(format));
		 for (int i = 1; i <= days; i++) {
            DateTime offsetDay = DateUtil.offsetDay(startDate, i);
            dateList.add(offsetDay.toString(format));
        }
		return dateList;
	}
	
	public static List<String> queryStartMonthList(DateTime startDate,String format,int months){
	    List<String> dateList = new ArrayList<String>();
	    dateList.add(startDate.toString(format));
	    for (int i = 1; i <= months; i++) {
	        DateTime offsetDay = DateUtil.offsetMonth(startDate, i);
	        dateList.add(offsetDay.toString(format));
	    }
	    return dateList;
	}
	
	public static List<List<String>> splitSubListBySize(List<String> dateList,int pageSize){
		return ListUtils.partition(dateList, pageSize);
	}
}
