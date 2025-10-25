package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;


@Getter
public enum SubjectEnum {
	
	WULI("wuli", "物理"),
    
	HUAXUE("huaxue", "化学"),
    
	SHENGWU("shengwu", "生物"),

    ;
    @EnumValue
    private String code;
    @JsonValue
    private String name;

    SubjectEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public static SubjectEnum getSubjectEnumByCode(String code) {
		if(code == null) {
			return null;
		}
		for (SubjectEnum mt : SubjectEnum.values()) {
			if(mt.getCode().equals(code)) {
				return mt;
			}
		}
		return null;
	}
}
