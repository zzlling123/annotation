package com.xinkao.erp.exam.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinkao.erp.common.model.entity.BaseEntity;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.exam.model.vo.ExamPageUserQuestionVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExamPageAnswerVo extends BaseEntity implements OutputConverter<ExamPageAnswerVo, Exam> {

    private Integer examId;

    private Integer userId;

    private Integer answerStatus;

    private String answerTs;

    private String startTs;

    private String endTs;

    private String score;

    private String scoreTs;

    private Integer passStatus;

    private Integer onCorrect;

    private Integer needCorrect;

    List<ExamPageUserQuestionVo> examPageUserQuestionVoList;
}