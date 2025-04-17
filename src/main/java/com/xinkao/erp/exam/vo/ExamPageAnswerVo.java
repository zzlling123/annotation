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
@ApiModel("用户作答详情")
public class ExamPageAnswerVo extends BaseEntity implements OutputConverter<ExamPageAnswerVo, Exam> {

    /**
     * 考试项目主键
     */
    @ApiModelProperty("考试项目主键")
    private Integer examId;

    /**
     * 用户主键
     */
    @ApiModelProperty("用户主键")
    private Integer userId;

    /**
     * 作答状态: 0-未做答 1-进行中 2-已提交
     */
    @ApiModelProperty("作答状态: 0-未做答 1-进行中 2-已提交")
    private Integer answerStatus;

    /**
     * (汇总出分)提交时间
     */
    @ApiModelProperty("提交时间")
    private String answerTs;

    /**
     * 答题开始时间
     */
    @ApiModelProperty("答题开始时间")
    private String startTs;


    /**
     * 答题结束时间
     */
    @ApiModelProperty("答题结束时间")
    private String endTs;

    /**
     * 最后得分
     */
    @ApiModelProperty("最后得分")
    private String score;

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
     * 是否已经全部批改0否1是
     */
    @ApiModelProperty("是否已经全部批改0否1是")
    private Integer onCorrect;

    /**
     * 是否需要批改0否1是
     */
    @ApiModelProperty("是否需要批改0否1是")
    private Integer needCorrect;

    //题目列表及答案
    @ApiModelProperty("学生题目列表及答案")
    List<ExamPageUserQuestionVo> examPageUserQuestionVoList;
}