package com.xinkao.erp.core.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 业务信息告警
 * @author hys_thanks
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DingtalkInfoContentRequest implements Serializable {

    private static final long serialVersionUID = -8706987329455574737L;

    private static String KEYWORD = "信息通知：";

    private String content;

    public void buildContent(String content) {
        this.content = KEYWORD + content;
    }

}
