package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 题库表
 * </p>
 *
 * @author Ldy
 * @since 2025-03-22 11:19:40
 */
@Getter
@Setter
@TableName("q_question")
public class Question extends DataEntity {

    /**
     * 题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
     */
    @TableField("shape")
    private Integer shape;

    /**
     * 题目分类
     */
    @TableField("type")
    private Integer type;

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
     * 操作题标头
     */
    @TableField("title")
    private String title;

    /**
     * 题干,json列表
     */
    @TableField("question")
    private String question;

    /**
     * 操作题文件路径JSON
     */
    @TableField("json_url")
    private String jsonUrl;

    /**
     * 题干纯文字
     */
    @TableField("question_text")
    private String questionText;

    /**
     * 选项列表 json["A","B","C"]
     */
    @TableField("options")
    private String options;

    /**
     * 答案A
     */
    @TableField("answer")
    private String answer;

    /**
     * 如果是填空题，则为有几个空
     */
    @TableField("answer_count")
    private Integer answerCount;

    /**
     * 答案说明
     */
    @TableField("answer_tip")
    private String answerTip;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 是否可用:1-可用 0-不可用
     */
    @TableField("state")
    private Integer state;

    /**
     * 是否需要批改0否1是
     */
    @TableField("need_correct")
    private Integer needCorrect;

    /**
     * 是否练习用题0否1是
     */
    @TableField("for_exercise")
    private Integer forExercise;

    //是否题目单，0否1是
    @TableField("is_form")
    private Integer isForm;

    /**
     * 文档路径
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 状态:0-正常 1-删除
     */
    @TableField("is_del")
    private Integer isDel;

    @TableField(exist = false)
    private String score;

    @TableField(exist = false)
    private String scorePart;

    @TableField("estimated_time")
    private Integer estimatedTime;

    @TableField("exercise_class_ids")
    private String exerciseClassIds;

}
