package com.xinkao.erp.common.util;

import com.xinkao.erp.common.exception.BusinessException;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName StrToSqlTimeUtil
 * @Description TODO
 * @Author youmin
 * @Date 2023/8/21 16:03
 * @Version 1.0
 */
@Component
public class StrToSqlTimeUtil {
    public static final String HH_MM_FORMAT = "HH:mm";

    public static final String TIME_PERIOD_CONNECTOR = "-";

    @PostConstruct
    public void init() {
        StrToSqlTimeUtil strToT = this;
    }

    public static Time strToTime(String strTime) {
        return new Time(strToDate(strTime).getTime());
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat(HH_MM_FORMAT);
        Date d;
        try {
            d = format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new BusinessException("时间转换错误!");
        }
        if (d == null) {
            throw new BusinessException("时间转换为空!");
        }
        return d;
    }



}
