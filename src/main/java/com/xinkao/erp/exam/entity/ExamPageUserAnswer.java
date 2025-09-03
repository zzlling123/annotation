package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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
     * 批改人ID
     */
    @TableField("correct_id")
    private Integer correctId;

    /**
     * 批改时间
     */
    @TableField("correct_time")
    private Date correctTime;

    //`biao` INT DEFAULT 0 COMMENT '正确标注个数',
    //`cuo` INT DEFAULT 0 COMMENT '应该标注未标注个数',
    //`wu` INT DEFAULT 0 COMMENT '错误标注个数',
    //`shu` INT DEFAULT 0 COMMENT '属性个数',
    //`zong` INT DEFAULT 0 COMMENT '总共需要标注个数',
    //`da` INT DEFAULT 0 COMMENT '学生标注个数',
    //`accuracy_rate` DECIMAL(5,2) NULL COMMENT '标注准确率 = biao / da',
    //`coverage_rate` DECIMAL(5,2) NULL COMMENT '标注准确覆盖率 = biao / zong',

    /**
     * 正确标注个数
     */
    @TableField("biao")
    private Integer biao;

    /**
     * 应该标注未标注个数
     */
    @TableField("cuo")
    private Integer cuo;

    /**
     * 错误标注个数
     */
    @TableField("wu")
    private Integer wu;

    /**
     * 属性个数
     */
    @TableField("shu")
    private Integer shu;

    /**
     * 总共需要标注个数
     */
    @TableField("zong")
    private Integer zong;

    /**
     * 学生标注个数
     */
    @TableField("da")
    private Integer da;

    /**
     * 标注准确率 = biao / da
     */
    @TableField("accuracy_rate")
    private BigDecimal accuracyRate;

    @TableField("coverage_rate")
    private BigDecimal coverageRate;

    /**
     * 是否需要批改0否1是
     */
    @TableField("need_correct")
    private Integer needCorrect;


}
