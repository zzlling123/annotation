package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("q_question")
public class Question extends DataEntity {

    @TableField("shape")
    private Integer shape;

    @TableField("type")
    private Integer type;

    @TableField("difficulty_level")
    private Integer difficultyLevel;

    @TableField(value = "difficulty_point_id", insertStrategy = FieldStrategy.IGNORED)
    private Integer difficultyPointId;

    @TableField("symbol")
    private String symbol;

    @TableField("title")
    private String title;

    @TableField("question")
    private String question;

    @TableField("json_url")
    private String jsonUrl;

    @TableField("question_text")
    private String questionText;

    @TableField("options")
    private String options;

    @TableField("answer")
    private String answer;

    @TableField("answer_count")
    private Integer answerCount;

    @TableField("answer_tip")
    private String answerTip;

    @TableField("sort")
    private Integer sort;

    @TableField("state")
    private Integer state;

    @TableField("need_correct")
    private Integer needCorrect;

    @TableField("for_exercise")
    private Integer forExercise;

    @TableField("is_form")
    private Integer isForm;

    @TableField("file_url")
    private String fileUrl;

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
