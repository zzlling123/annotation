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
public class ExamPageVo extends BaseEntity implements OutputConverter<ExamPageVo, Exam> {

    private String id;

    private String examName;

    private Integer state;

    private Integer rollMakeOver;

    private String difficultyLevel;

    private String symbol;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
}