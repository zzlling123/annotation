package com.xinkao.erp.common.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

/**
 * 查询时间处理工具类
 * @author hys_thanks
 */
public class QueryDateUtil {
	/**
	 * 查询两个日期间隔的天数
	 * @param startDate
	 * @param endDate
	 * @return 如果等于0,当天;如果小于0,开始时间与结束时间晚;
	 */
	public static Integer betweenDay(DateTime startDate,DateTime endDate) {
         long betweenDay = DateUtil.betweenDay(startDate, endDate, true);
         int intValue = Long.valueOf(betweenDay).intValue();
         if(startDate.after(endDate)) {
        	 return -intValue;
         }
         return intValue;
	}
	/**
	 * 查询两个日期间隔的月数
	 * @param startDate
	 * @param endDate
	 * @return 如果等于0,当月;如果小于0,开始时间与结束时间晚;
	 */
	public static Integer betweenMonth(DateTime startDate,DateTime endDate) {
	    long betweenDay = DateUtil.betweenMonth(startDate, endDate, true);
	    int intValue = Long.valueOf(betweenDay).intValue();
	    if(startDate.after(endDate)) {
	        return -intValue;
	    }
	    return intValue;
	}
	/**
	 * 查询开始日期之后的天数(含开始日期)
	 * @param startDate
	 * @return
	 */
	public static List<String> queryStartDayList(DateTime startDate,String format,int days){
		 List<String> dateList = new ArrayList<String>();
		 dateList.add(startDate.toString(format));
		 for (int i = 1; i <= days; i++) {
            DateTime offsetDay = DateUtil.offsetDay(startDate, i);
            dateList.add(offsetDay.toString(format));
        }
		return dateList;
	}
	/**
	 * 查询开始日期之后的月数(含开始月)
	 * @param startDate
	 * @return
	 */
	public static List<String> queryStartMonthList(DateTime startDate,String format,int months){
	    List<String> dateList = new ArrayList<String>();
	    dateList.add(startDate.toString(format));
	    for (int i = 1; i <= months; i++) {
	        DateTime offsetDay = DateUtil.offsetMonth(startDate, i);
	        dateList.add(offsetDay.toString(format));
	    }
	    return dateList;
	}
	/**
	 * 将日期列表按照分页大小进行拆分
	 * @param dateList
	 * @param pageSize
	 * @return
	 */
	public static List<List<String>> splitSubListBySize(List<String> dateList,int pageSize){
		return ListUtils.partition(dateList, pageSize);
	}
}
