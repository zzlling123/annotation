package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 考试设置表
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@Getter
@Setter
@TableName("exam_page_set")
public class ExamPageSet extends DataEntity {

    /**
     * 考试ID
     */
    @TableField("exam_id")
    private Integer examId;

    /**
     * 总分
     */
    @TableField("score")
    private Integer score;

    /**
     * 合格分数
     */
    @TableField("score_pass")
    private Integer scorePass;

    /**
     * 生成方式:0-同题同序 1-同题不同序 2-不同题不同序
     */
    @TableField("page_mode")
    private Integer pageMode;

    /**
     * 试卷排版:0-试题全页显示 1-一页一道题
     */
    @TableField("flip_type")
    private Integer flipType;

    /**
     * 题目个数
     */
    @TableField("question_count")
    private Integer questionCount;

    /**
     * 题目分布导入状态:0-未导入 1-已导入
     */
    @TableField("question_status")
    private Integer questionStatus;


}
