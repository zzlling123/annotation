package com.xinkao.erp.question.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
public class QuestionPageVo extends BaseEntity {

    private String id;

    private String title;

    private String question;

    private String questionText;

    private String shape;

    private String type;

    private String difficultyLevel;

    private String difficultyPointId;

    private String symbol;

    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    private Integer needCorrect;

    private Integer forExercise;

    private String fileUrl;

    private Integer estimatedTime;

    private String exerciseClassIds;

}