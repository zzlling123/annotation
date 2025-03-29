package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 考生答题时间心跳
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@Getter
@Setter
@TableName("exam_page_user_log")
public class ExamPageUserLog extends DataSnowIdEntity {

    /**
     * 问卷主键
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 考试项目主键
     */
    @TableField("exam_id")
    private Integer examId;

    /**
     * 累计答题时长
     */
    @TableField("exam_length")
    private Integer examLength;

    /**
     * 答题开始时间
     */
    @TableField("start_ts")
    private String startTs;

    /**
     * 最新答题时间
     */
    @TableField("last_update_ts")
    private String lastUpdateTs;

    /**
     * 提交状态:0-未提交 1-已提交
     */
    @TableField("submit_status")
    private Integer submitStatus;


}
