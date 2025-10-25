package com.xinkao.erp.exam.excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel("考试设置题目数量")
public class ExamPageSetVo{

    @ApiModelProperty("题目分类ID")
    private Integer type;

    @ApiModelProperty("题目分类名称")
    private String typeStr;

    @ApiModelProperty("单选题库题数")
    private String choiceSingleCount = "0";

    /*
    单选抽取题数
     */
    @ApiModelProperty("单选抽取题数")
    private String choiceSingleChouCount;

    @ApiModelProperty("单选每题得分")
    private String choiceSingleScore;

    @ApiModelProperty("多选题库题数")
    private String choiceMultiCount = "0";

    @ApiModelProperty("多选抽取题数")
    private String choiceMultiChouCount;

    @ApiModelProperty("多选每题得分")
    private String choiceMultiScore;

    @ApiModelProperty("多选部分每题得分")
    private String choiceMultiPerPart;

    @ApiModelProperty("填空题库题数")
    private String choiceFillCount = "0";

    @ApiModelProperty("填空抽取题数")
    private String choiceFillChouCount;

    @ApiModelProperty("填空每题得分")
    private String choiceFillScore;

    @ApiModelProperty("问答题库题数")
    private String choiceAnswerCount = "0";

    @ApiModelProperty("问答抽取题数")
    private String choiceAnswerChouCount;

    @ApiModelProperty("问答每题得分")
    private String choiceAnswerScore;

    @ApiModelProperty("实践题库题数")
    private String choicePracticeCount = "0";

    @ApiModelProperty("实践抽取题数")
    private String choicePracticeChouCount;

    @ApiModelProperty("实践每题得分")
    private String choicePracticeScore;

    @ApiModelProperty("题目单库题数")
    private String choiceFormCount = "0";

    @ApiModelProperty("题目单抽取题数")
    private String choiceFormChouCount;

    @ApiModelProperty("题目单每题得分")
    private String choiceFormScore;

    @ApiModelProperty("判断题库题数")
    private String choiceJudgeCount = "0";

    @ApiModelProperty("判断题抽取题数")
    private String choiceJudgeChouCount;

    @ApiModelProperty("判断题每题得分")
    private String choiceJudgeScore;
}
