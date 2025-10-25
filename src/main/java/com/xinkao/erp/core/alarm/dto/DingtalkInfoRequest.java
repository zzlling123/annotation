package com.xinkao.erp.core.alarm.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DingtalkInfoRequest implements Serializable {

    private static final long serialVersionUID = 5720875394178824032L;

    @Builder.Default
    private String msgtype = "text";

    private DingtalkInfoContentRequest text;

}
