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
@ApiModel("考试分页视图")
public class ExamPageVo extends BaseEntity implements OutputConverter<ExamPageVo, Exam> {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("考试名称")
    private String examName;

    @ApiModelProperty("考试状态")
    private Integer state;
    
    @ApiModelProperty("组卷进度是否完成，0否1是")
    private Integer rollMakeOver;

    @ApiModelProperty("难度")
    private String difficultyLevel;

    @ApiModelProperty("题目所属标记")
    private String symbol;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty("开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty("结束时间")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty("创建时间")
    private Date createTime;
}