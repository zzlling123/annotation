package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/**
 * 科目相关的常量集
 * @author hys_thanks
 */
@Getter
public enum SubjectEnum {
	/**
     * 物理
     */
	WULI("wuli", "物理"),
    /**
     * 化学
     */
	HUAXUE("huaxue", "化学"),
    /**
     * 商户
     */
	SHENGWU("shengwu", "生物"),
    //构造
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
