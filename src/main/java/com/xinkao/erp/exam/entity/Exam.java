package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 考试表
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@Getter
@Setter
@TableName("exam")
public class Exam extends DataEntity {

    /**
     * 考试名称
     */
    @TableField("exam_name")
    private String examName;

    /**
     * 考试开始时间
     */
    @TableField("start_time")
    private Date startTime;

    /**
     * 考试结束时间
     */
    @TableField("end_time")
    private Date endTime;

    /**
     * 考试时长（分钟）
     */
    @TableField("duration")
    private Integer duration;

    /**
     * 考试状态: 0-待发布 10-未开始 20-考试进行中 21-考试已结束
     */
    @TableField("state")
    private Integer state;

    /**
     * 难易程度：0-容易 1-中等 2-较难
     */
    @TableField("difficulty_level")
    private Integer difficultyLevel;

    /**
     * 标记
     */
    @TableField("symbol")
    private String symbol;

    /**
     * 组卷进度是否完成，0否1是
     */
    @TableField("roll_make_over")
    private Integer rollMakeOver;

    /**
     * 是否删除0否1是
     */
    @TableField("is_del")
    private Integer isDel;

    /**
     * 是否专家评审0否1是
     */
    @TableField("is_expert")
    private Integer isExpert;


}
