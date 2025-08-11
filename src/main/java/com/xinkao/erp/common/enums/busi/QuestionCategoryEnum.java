package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QuestionCategoryEnum {

    IMAGE_LABELING(1, "图像标注"),
    POINT_CLOUD_3D_LABELING(2, "3D点云标注"),
    OCR_LABELING(3, "OCR标注"),
    VOICE_LABELING(4, "语音标注"),
    LABELING_2D(5, "2D标注"),
    FACE_KEYPOINT_LABELING(6, "人脸关键点标注"),
    LABELING_2D_3D(7, "2D+3D点云标注");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String name;

    QuestionCategoryEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static QuestionCategoryEnum of(String code) {
        for (QuestionCategoryEnum category : QuestionCategoryEnum.values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    // 根据name获取code
    public static Integer getCodeByName(String name) {
        for (QuestionCategoryEnum category : QuestionCategoryEnum.values()) {
            if (category.getName().equals(name)) {
                return category.getCode();
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
}