package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 考生表
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@Getter
@Setter
@TableName("exam_page_user")
public class ExamPageUser extends DataSnowIdEntity {

    /**
     * 考试项目主键
     */
    @TableField("exam_id")
    private Integer examId;

    /**
     * 用户主键
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 选题状态:0-未选题 1-选题中 2-选题完成
     */
    @TableField("select_status")
    private Integer selectStatus;

    /**
     * 作答状态: 0-未做答 1-进行中 2-已提交
     */
    @TableField("answer_status")
    private Integer answerStatus;

    /**
     * (汇总出分)提交时间
     */
    @TableField("answer_ts")
    private String answerTs;

    /**
     * 答题开始时间
     */
    @TableField("start_ts")
    private String startTs;


    /**
     * 答题结束时间
     */
    @TableField("end_ts")
    private String endTs;

    /**
     * 最后得分
     */
    @TableField("score")
    private Integer score;

    /**
     * 出分时间
     */
    @TableField("score_ts")
    private String scoreTs;

    /**
     * 合格状态:0-不合格 1-合格
     */
    @TableField("pass_status")
    private Integer passStatus;


}
