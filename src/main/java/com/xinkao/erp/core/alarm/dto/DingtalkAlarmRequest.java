package com.xinkao.erp.core.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 异常告警发送到钉钉的信息拼装
 * @author hys_thanks
 *
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DingtalkAlarmRequest implements Serializable {

    private static final long serialVersionUID = 5720875394178824032L;

    @Builder.Default
    private String msgtype = "text";

    private DingtalkAlarmContentRequest text;

}
