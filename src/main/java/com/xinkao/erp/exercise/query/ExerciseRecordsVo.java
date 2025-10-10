package com.xinkao.erp.exercise.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ApiModel("练习记录表-数据统计")
public class ExerciseRecordsVo {
    private Integer id;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String remark;
    @ApiModelProperty("关联到用户的ID")
    private Integer userId;

    @ApiModelProperty("用户名")
    private String realName;

    @ApiModelProperty("关联到练习模块的ID")
    private Integer moduleId;

    @ApiModelProperty("shape 题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题")
    private Integer shape;

    @ApiModelProperty("question_score 每道题的分数")
    private Integer questionScore;

    @ApiModelProperty("练习开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty("练习结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty("练习时长分钟")
    private Long duration;

    @ApiModelProperty("题目完成情况")
    private Integer completionStatus;

    @ApiModelProperty("练习分数")
    private Integer score;

    @ApiModelProperty("练习反馈信息")
    private String feedback;

    @ApiModelProperty("正确标注个数")
    private Integer biao;

    @ApiModelProperty("应该标注未标注个数")
    private Integer cuo;

    @ApiModelProperty("错误标注个数")
    private Integer wu;

    @ApiModelProperty("属性个数")
    private Integer shu;

    @ApiModelProperty("总共需要标注个数")
    private Integer zong;

    @ApiModelProperty("学生标注个数")
    private Integer da;

    @ApiModelProperty("标注准确率 = biao / da")
    private BigDecimal accuracyRate;

    @ApiModelProperty("标注准确覆盖率 = biao / zong")
    private BigDecimal coverageRate;

}
