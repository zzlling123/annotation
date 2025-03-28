package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
/**
 * 生成常量自增id业务常量
 * 字典常量
 **/
@Getter
public enum GlobleSqlTypeEnum {
	, //

    //构造
    ;

    @EnumValue
    private final String code;
    /**
     * 长度为1-9,不能大于10
     */
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