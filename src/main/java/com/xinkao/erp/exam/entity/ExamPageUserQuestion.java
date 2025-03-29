package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 试卷表
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:06:02
 */
@Getter
@Setter
@TableName("exam_page_user_question")
public class ExamPageUserQuestion extends DataSnowIdEntity {

    /**
     * 试卷主键
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 考试项目主键
     */
    @TableField("exam_id")
    private Integer examId;

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
    @TableField("type_id")
    private Integer typeId;

    /**
     * 题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
     */
    @TableField("shape")
    private String shape;

    /**
     * 题干,json列表
     */
    @TableField("question")
    private String question;

    /**
     * 选项列表 json
     */
    @TableField("options")
    private String options;

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
     * 答案
     */
    @TableField("answer")
    private String answer;


}
