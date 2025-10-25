package com.xinkao.erp.question.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class QuestionExercisePageVo extends BaseEntity {

    private String id;

    private String title;

    private String question;

    private String questionText;

    @TableField("题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题")
    private String shape;

    private String type;

    private String difficultyLevel;

    private Integer state;

    private Integer symbol;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    private Integer needCorrect;

    private Integer forExercise;

    private String fileUrl;

    private Integer estimatedTime;

    private Integer exerciseState;
}