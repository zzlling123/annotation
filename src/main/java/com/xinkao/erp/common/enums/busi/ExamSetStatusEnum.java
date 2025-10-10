package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum ExamSetStatusEnum {
    STUDENT(1, "考生维护"),
    QEUSTION(2, "试题设置"),
    TIMER(3, "场次设置"),
    SITE(4, "考点设置"),
    PERSON(5, "监考安排"),
    GROUP(6, "分组"),
    ;
    @EnumValue
    private Integer code;
    @JsonValue
    private String name;

    ExamSetStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public static ExamSetStatusEnum getExamStatusByCode(Integer code) {
		if(code == null) {
			return null;
		}
		for (ExamSetStatusEnum mt : ExamSetStatusEnum.values()) {
			if(mt.getCode().intValue() == code) {
				return mt;
			}
		}
		return null;
	}
}
