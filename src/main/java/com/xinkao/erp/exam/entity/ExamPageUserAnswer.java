package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * 考生作答表
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@Getter
@Setter
@TableName("exam_page_user_answer")
public class ExamPageUserAnswer extends DataSnowIdEntity {

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
     * 题目分类
     */
    @TableField("type")
    private Integer type;

    /**
     * 题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
     */
    @TableField("shape")
    private Integer shape;

    /**
     * 正确答案
     */
    @TableField("right_answer")
    private String rightAnswer;

    /**
     * 题目分数
     */
    @TableField("score")
    private Integer score;

    /**
     * 部分答对分数(限多选)
     */
    @TableField("score_part")
    private Integer scorePart;

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
    private Integer userScore;

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

    /**
     * 是否需要批改0否1是
     */
    @TableField("need_correct")
    private Integer needCorrect;


}
