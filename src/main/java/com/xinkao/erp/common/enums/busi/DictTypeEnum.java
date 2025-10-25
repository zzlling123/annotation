package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


@Getter
public enum DictTypeEnum {

    ;

    @EnumValue
    private final String code;

    @JsonValue
    private final String name;

    DictTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
