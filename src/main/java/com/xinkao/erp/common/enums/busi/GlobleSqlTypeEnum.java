package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum GlobleSqlTypeEnum {
	, //

    ;

    @EnumValue
    private final String code;
    @EnumValue
    private final int length;

    @JsonValue
    private final String name;

    GlobleSqlTypeEnum(String code,int length, String name) {
        this.code = code;
        this.length = length;
        this.name = name;
    }
}