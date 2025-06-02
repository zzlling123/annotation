package com.xinkao.erp.summary.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinkao.erp.exam.entity.ExamPageUserAnswer;
import com.xinkao.erp.exam.model.vo.ExamPageUserVo;
import com.xinkao.erp.exercise.entity.InstantFeedbacks;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ClassSummaryParam {

    //考试id
     @ApiModelProperty("考试id，为null，则为练习")
    private Integer examId;

    //标注类型
     @ApiModelProperty("标注类型")
    private Integer type;

    //题目类型
    @ApiModelProperty("题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题")
    private String shape;

    //平均分
    @ApiModelProperty("平均分")
    private String avgScore;

    //最高分
    @ApiModelProperty("最高分")
    private String maxScore;

    //最低分
    @ApiModelProperty("最低分")
    private String minScore;

    @ApiModelProperty("平均用时")
    private Double avgDuration;

    @ApiModelProperty("平均覆盖率")
    private Double avgCoverage;

    @ApiModelProperty("平均准确率")
    private Double avgAccuracy;

    @ApiModelProperty("正确标注个数")
    private Integer avgBiao;

    @ApiModelProperty("应该标注未标注个数")
    private Integer avgCuo;

    @ApiModelProperty("错误标注个数")
    private Integer avgWu;

    @ApiModelProperty("属性个数")
    private Integer avgShu;

    @ApiModelProperty("总共需要标注个数")
    private Integer avgZong;

    @ApiModelProperty("学生标注个数")
    private Integer avgDa;

    //学生考试详情
    @ApiModelProperty("学生考试详情")
    private List<ExamPageUserVo> examPageUserVoList;

    //学生考试详情
    @ApiModelProperty("学生考试做题详情")
    private List<ExamPageUserAnswer> examPageUserAnswerList;

    //学生练习详情
    @ApiModelProperty("学生练习详情")
    private List<InstantFeedbacks> instantFeedbacksVoList;

}
