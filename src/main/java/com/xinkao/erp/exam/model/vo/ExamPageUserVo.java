package com.xinkao.erp.exam.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@Data
public class ExamPageUserVo {

    
    @ApiModelProperty("考试项目主键")
    @TableField("exam_id")
    private Integer examId;

    
    @ApiModelProperty("用户主键")
    @TableField("user_id")
    private Integer userId;

    
    @ApiModelProperty("用户名称")
    @TableField("real_name")
    private String realName;

    
    @ApiModelProperty("班级主键")
    @TableField("class_id")
    private Integer classId;

    
    @ApiModelProperty("题状态:0-未选题 1-选题中 2-选题完成")
    @TableField("select_status")
    private Integer selectStatus;

    
    @ApiModelProperty("作答状态: 0-未做答 1-进行中 2-已提交")
    @TableField("answer_status")
    private Integer answerStatus;

    
    @ApiModelProperty("(汇总出分)提交时间")
    @TableField("answer_ts")
    private String answerTs;

    
    @ApiModelProperty("答题开始时间")
    @TableField("start_ts")
    private String startTs;


    
    @ApiModelProperty("答题结束时间")
    @TableField("end_ts")
    private String endTs;

    
    @ApiModelProperty("最后得分")
    @TableField("score")
    private BigDecimal score;

    
    @ApiModelProperty("出分时间")
    @TableField("score_ts")
    private String scoreTs;

    
    @ApiModelProperty("合格状态:0-不合格 1-合格")
    @TableField("pass_status")
    private Integer passStatus;

    
    @ApiModelProperty("是否已经全部批改0否1是")
    @TableField("on_correct")
    private Integer onCorrect;

    
    @ApiModelProperty("是否需要批改0否1是")
    @TableField("need_correct")
    private Integer needCorrect;
}
