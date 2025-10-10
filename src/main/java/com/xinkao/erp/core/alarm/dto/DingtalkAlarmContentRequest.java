package com.xinkao.erp.core.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DingtalkAlarmContentRequest implements Serializable {

    private static final long serialVersionUID = -8706987329455574737L;

    private static String KEYWORD = "异常告警：";

    private String content;

    public void buildContent(String content) {
        this.content = KEYWORD + content;
    }

}
