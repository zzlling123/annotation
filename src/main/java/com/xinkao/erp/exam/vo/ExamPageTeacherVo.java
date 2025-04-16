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
@ApiModel("待批改分页视图")
public class ExamPageTeacherVo extends BaseEntity implements OutputConverter<ExamPageTeacherVo, Exam> {

    @ApiModelProperty("考试ID")
    private String examId;

    @ApiModelProperty("考试名称")
    private String examName;

    @ApiModelProperty("考试状态")
    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty("开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty("结束时间")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("已批阅人数")
    private Integer correctNum;

    @ApiModelProperty("应批阅人数")
    private Integer shouldCorrectNum;
}