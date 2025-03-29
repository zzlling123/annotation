package com.xinkao.erp.exam.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.entity.BaseEntity;
import com.xinkao.erp.exam.entity.ExamClass;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ApiModel("考试详情视图")
public class ExamDetailVo extends BaseEntity {

    @ApiModelProperty("考试名称")
    private String examName;

    @ApiModelProperty("考试状态")
    private Integer status;

    @ApiModelProperty("考试总分")
    private Integer score;

    @ApiModelProperty("考试及格分")
    private Integer scorePass;

    @ApiModelProperty("生成方式:0-同题同序 1-同题不同序 2-不同题不同序")
    private Integer pageMode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty("开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty("结束时间")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("班级列表")
    private List<ExamClass> classList;
}