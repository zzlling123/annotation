package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QuestionDifficultyEnum {

    LEVEL_ONE(1, "一级"),
    LEVEL_TWO(2, "二级"),
    LEVEL_THREE(3, "三级"),
    LEVEL_FOUR(4, "四级"),
    LEVEL_FIVE(5, "五级");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String name;

    QuestionDifficultyEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static QuestionDifficultyEnum of(Integer code) {
        for (QuestionDifficultyEnum difficulty : QuestionDifficultyEnum.values()) {
            if (difficulty.getCode().equals(code)) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    public static Integer getCodeByName(String name) {
        for (QuestionDifficultyEnum difficulty : QuestionDifficultyEnum.values()) {
            if (difficulty.getName().equals(name)) {
                return difficulty.getCode();
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
}