package com.xinkao.erp.question.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.entity.BaseEntity;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.question.entity.Label;
import com.xinkao.erp.question.entity.Question;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class QuestionInfoVo extends BaseEntity implements OutputConverter<QuestionInfoVo, Question> {

    private String title;

    private String question;

    private String questionText;

    private String answer;

    private String jsonUrl;

    private List<String> options;

    private Integer answerCount;

    private String answerTip;

    private String shape;

    private String type;

    private String difficultyLevel;

    private String difficultyPointId;

    private String symbol;

    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    private Integer needCorrect;

    private List<LabelVo> labelList;

    private Integer forExercise;

    private String fileUrl;

    private Integer estimatedTime;

    private String exerciseClassIds;

    private List<Mark> markList;
}