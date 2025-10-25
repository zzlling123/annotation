package com.xinkao.erp.exam.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.entity.BaseEntity;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.exam.entity.Exam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ExamPageUserListVo extends BaseEntity implements OutputConverter<ExamPageUserListVo, Exam> {

    private String id;

    private String examId;

    private String userId;

    private String realName;

    private Integer answerStatus;

    private Integer onCorrect;

    private Integer needCorrect;
}