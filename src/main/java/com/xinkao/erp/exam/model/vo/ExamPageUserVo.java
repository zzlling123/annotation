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

/**
 * <p>
 * 考生考试信息表
 * </p>
 *
 */
@Getter
@Setter
@Data
public class ExamPageUserVo {

    /**
     * 考试项目主键
     */
    @ApiModelProperty("考试项目主键")
    @TableField("exam_id")
    private Integer examId;

    /**
     * 用户主键
     */
    @ApiModelProperty("用户主键")
    @TableField("user_id")
    private Integer userId;

    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    @TableField("real_name")
    private String realName;

    /**
     * 班级主键
     */
    @ApiModelProperty("班级主键")
    @TableField("class_id")
    private Integer classId;

    /**
     * 选题状态:0-未选题 1-选题中 2-选题完成
     */
    @ApiModelProperty("题状态:0-未选题 1-选题中 2-选题完成")
    @TableField("select_status")
    private Integer selectStatus;

    /**
     * 作答状态: 0-未做答 1-进行中 2-已提交
     */
    @ApiModelProperty("作答状态: 0-未做答 1-进行中 2-已提交")
    @TableField("answer_status")
    private Integer answerStatus;

    /**
     * (汇总出分)提交时间
     */
    @ApiModelProperty("(汇总出分)提交时间")
    @TableField("answer_ts")
    private String answerTs;

    /**
     * 答题开始时间
     */
    @ApiModelProperty("答题开始时间")
    @TableField("start_ts")
    private String startTs;


    /**
     * 答题结束时间
     */
    @ApiModelProperty("答题结束时间")
    @TableField("end_ts")
    private String endTs;

    /**
     * 最后得分
     */
    @ApiModelProperty("最后得分")
    @TableField("score")
    private BigDecimal score;

    /**
     * 出分时间
     */
    @ApiModelProperty("出分时间")
    @TableField("score_ts")
    private String scoreTs;

    /**
     * 合格状态:0-不合格 1-合格
     */
    @ApiModelProperty("合格状态:0-不合格 1-合格")
    @TableField("pass_status")
    private Integer passStatus;

    /**
     * 是否已经全部批改0否1是
     */
    @ApiModelProperty("是否已经全部批改0否1是")
    @TableField("on_correct")
    private Integer onCorrect;

    /**
     * 是否需要批改0否1是
     */
    @ApiModelProperty("是否需要批改0否1是")
    @TableField("need_correct")
    private Integer needCorrect;
}
