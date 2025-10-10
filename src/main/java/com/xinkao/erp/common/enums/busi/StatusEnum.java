package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusEnum {

    AVAILABLE(1, "可用"),
    UNAVAILABLE(0, "不可用");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String name;

    StatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static StatusEnum of(Integer code) {
        for (StatusEnum status : StatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    public static Integer getCodeByName(String name) {
        for (StatusEnum status : StatusEnum.values()) {
            if (status.getName().equals(name)) {
                return status.getCode();
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
}