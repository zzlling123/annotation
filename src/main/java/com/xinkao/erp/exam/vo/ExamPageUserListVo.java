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
@ApiModel("待批改用户列表视图")
public class ExamPageUserListVo extends BaseEntity implements OutputConverter<ExamPageUserListVo, Exam> {

    @ApiModelProperty("考生试卷ID")
    private String id;

    @ApiModelProperty("考试ID")
    private String examId;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("用户名称")
    private String realName;

    @ApiModelProperty("是否已经全部批改0否1是")
    private Integer onCorrect;

    @ApiModelProperty("是否需要批改0否1是")
    private Integer needCorrect;
}