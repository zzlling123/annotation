package com.xinkao.erp.common.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.dozer.DozerConverter;

public class LocalDateTimeToDateDozerConverter extends DozerConverter<LocalDateTime, Date> {

    public LocalDateTimeToDateDozerConverter() {
        super(LocalDateTime.class, Date.class);
    }

    @Override
    public LocalDateTime convertFrom(Date source, LocalDateTime destination) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
        return dateTime;
    }

    @Override
    public Date convertTo(LocalDateTime source, Date destination) {
        Date convertToDate = Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
        return convertToDate;
    }

}