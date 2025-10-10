package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EntitySystemEnum {

    HUMAN_RESOURCES_AGENCY(1, "人社局"),
    SCHOOL(2, "学校");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String name;

    EntitySystemEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static EntitySystemEnum of(Integer code) {
        for (EntitySystemEnum system : EntitySystemEnum.values()) {
            if (system.getCode().equals(code)) {
                return system;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    public static Integer getCodeByName(String name) {
        for (EntitySystemEnum system : EntitySystemEnum.values()) {
            if (system.getName().equals(name)) {
                return system.getCode();
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
}