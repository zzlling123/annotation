package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum UserLevelEnum {
	/**
     * 学校
     */
    SCHOOL(10, "学校"),
    /**
     * 区县教育局
     */
    AREA(20, "区县教育局"),
    /**
     * 市局
     */
    CITY(30, "市局"),
    //构造
    ;
    @EnumValue
    private int code;
    @JsonValue
    private String name;

    UserLevelEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public static UserLevelEnum getLevelByCode(int code) {
		for (UserLevelEnum mt : UserLevelEnum.values()) {
			if(code == mt.getCode()) {
				return mt;
			}
		}
		return null;
	}
}
