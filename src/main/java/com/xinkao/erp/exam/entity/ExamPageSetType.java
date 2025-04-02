package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 试卷设置-类型分布表
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@Getter
@Setter
@TableName("exam_page_set_type")
public class ExamPageSetType extends DataEntity {

    /**
     * 考试ID
     */
    @TableField("exam_id")
    private String examId;

    /**
     * 分类ID
     */
    @TableField("type_id")
    private Integer typeId;

    /**
     * 分类名称
     */
    @TableField("type_name")
    private String typeName;

    /**
     * 题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
     */
    @TableField("shape")
    private String shape;

    /**
     * 该分类下该种题型题目数量
     */
    @TableField("question_num")
    private Integer questionNum;

    /**
     * 每题分数
     */
    @TableField("score")
    private Integer score;

    /**
     * 部分得分
     */
    @TableField("score_part")
    private Integer scorePart;


}
