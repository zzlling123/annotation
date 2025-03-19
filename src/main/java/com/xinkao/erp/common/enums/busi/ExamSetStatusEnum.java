package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/**
 * 考试进度相关常量集
 * @author hys_thanks
 */
@Getter
public enum ExamSetStatusEnum {
	/**
     * 考生维护
     */
    STUDENT(1, "考生维护"),
    /**
     * 考试试题选择
     */
    QEUSTION(2, "试题设置"),
    /**
     * 场次设置
     */
    TIMER(3, "场次设置"),
    /**
     * 考点设置
     */
    SITE(4, "考点设置"),
    /**
     *监考安排
     */
    PERSON(5, "监考安排"),
    /**
     *分组
     */
    GROUP(6, "分组"),
    //构造
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
