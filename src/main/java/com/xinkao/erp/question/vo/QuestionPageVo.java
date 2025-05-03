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
public class QuestionPageVo extends BaseEntity {

    @ApiModelProperty("题目Id")
    private String id;

    @ApiModelProperty("题目")
    private String question;

    @ApiModelProperty("题目text")
    private String questionText;

    @TableField("题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题")
    private String shape;

    @ApiModelProperty("题型")
    private String type;

    @ApiModelProperty("难度")
    private String difficultyLevel;

    @ApiModelProperty("是否启用0否1是")
    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @ApiModelProperty("是否需要批改0否1是")
    private Integer needCorrect;
}