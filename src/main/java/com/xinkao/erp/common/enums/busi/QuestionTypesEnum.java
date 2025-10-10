package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QuestionTypesEnum {

    
    DANXUAN(100, "单选题"),
    
    DUOXUAN(200, "多选题"),
    
    TIANKONG(300, "填空题"),
    
    ZHUGUAN(400, "主观题"),
    
    PANDUAN(700, "判断题"),
    
    TIMUDAN(600, "题目单");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String name;

    QuestionTypesEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static QuestionTypesEnum of(String code) {
        for (QuestionTypesEnum questionType : QuestionTypesEnum.values()) {
            if (questionType.getCode().equals(code)) {
                return questionType;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}