package com.xinkao.erp.exam.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ExamUserVo {

    @ApiModelProperty("考试项目主键")
    private Integer examId;

    @ApiModelProperty("考试项目名称")
    private String examName;

    
    @ApiModelProperty("用户主键")
    private Integer userId;

    
    @ApiModelProperty("题状态:0-未选题 1-选题中 2-选题完成")
    private Integer selectStatus;

    
    @ApiModelProperty("作答状态: 0-未做答 1-进行中 2-已提交")
    private Integer answerStatus;

    
    @ApiModelProperty("(汇总出分)提交时间")
    private String answerTs;

    
    @ApiModelProperty("最后得分")
    private BigDecimal score;

    
    @ApiModelProperty("出分时间")
    private String scoreTs;

    
    @ApiModelProperty("合格状态:0-不合格 1-合格")
    private Integer passStatus;

    
    @ApiModelProperty("考试状态: 0-待发布 10-未开始 20-考试进行中 21-考试已结束")
    private Integer state;

    
    @ApiModelProperty("考试开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date startTime;

    
    @ApiModelProperty("考试结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;

    
    private List<ExamPageUserQuestionVo> questionVoList;

    @ApiModelProperty("考试时长")
    private String duration;
}