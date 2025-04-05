package com.xinkao.erp.exam.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExamUserVo {

    @ApiModelProperty("考试项目主键")
    private Integer examId;

    /**
     * 用户主键
     */
    @ApiModelProperty("用户主键")
    private Integer userId;

    /**
     * 选题状态:0-未选题 1-选题中 2-选题完成
     */
    @ApiModelProperty("题状态:0-未选题 1-选题中 2-选题完成")
    private Integer selectStatus;

    /**
     * 作答状态: 0-未做答 1-进行中 2-已提交
     */
    @ApiModelProperty("作答状态: 0-未做答 1-进行中 2-已提交")
    private Integer answerStatus;

    /**
     * (汇总出分)提交时间
     */
    @ApiModelProperty("(汇总出分)提交时间")
    private String answerTs;

    /**
     * 最后得分
     */
    @ApiModelProperty("最后得分")
    private Integer score;

    /**
     * 出分时间
     */
    @ApiModelProperty("出分时间")
    private String scoreTs;

    /**
     * 合格状态:0-不合格 1-合格
     */
    @ApiModelProperty("合格状态:0-不合格 1-合格")
    private Integer passStatus;

    /**
     * 题目列表
     */
    private List<ExamPageUserQuestionVo> questionVoList;
}