package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 考生题目单子题作答表
 * </p>
 *
 * @author Ldy
 * @since 2025-07-27 18:48:01
 */
@Getter
@Setter
@TableName("exam_page_user_child_answer")
public class ExamPageUserChildAnswer extends DataSnowIdEntity {

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
     * exam_page_user_question试题主键
     */
    @TableField("question_id")
    private String questionId;

    /**
     * 题目子题ID
     */
    @TableField("question_child_id")
    private String questionChildId;

    /**
     * 是否上传文件题0否1是
     */
    @TableField("is_file")
    private Integer isFile;

    /**
     * 题号的数字格式
     */
    @TableField("num_sort")
    private Integer numSort;

    /**
     * 题号
     */
    @TableField("num")
    private String num;

    /**
     * 正确答案
     */
    @TableField("right_answer")
    private String rightAnswer;

    /**
     * 题目分数
     */
    @TableField("score")
    private BigDecimal score;

    /**
     * 部分答对分数(限多选)
     */
    @TableField("score_part")
    private BigDecimal scorePart;

    /**
     * 答题状态:0-未答题 1-已答题
     */
    @TableField("answer_status")
    private Integer answerStatus;

    /**
     * 用户答案
     */
    @TableField("user_answer")
    private String userAnswer;

    /**
     * 用户得分
     */
    @TableField("user_score")
    private BigDecimal userScore;

    /**
     * 是否需要批改(有问答题)0否1是
     */
    @TableField("need_correct")
    private Integer needCorrect;

    /**
     * 批改人ID
     */
    @TableField("correct_id")
    private Integer correctId;

    /**
     * 批改时间
     */
    @TableField("correct_time")
    private Date correctTime;


}
