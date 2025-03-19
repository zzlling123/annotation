package com.xinkao.erp.common.enums.system;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 日志状态
 **/
@Getter
public enum LogStatus {
    SUCCESS(0, "成功"), 
    ERROR(1, "异常");

    @EnumValue
    private final int code;

    @JsonValue
    private final String name;

    LogStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
