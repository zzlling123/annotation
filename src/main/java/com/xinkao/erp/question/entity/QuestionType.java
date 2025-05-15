package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 题库类型表
 * </p>
 *
 * @author Ldy
 * @since 2025-03-22 11:19:40
 */
@Getter
@Setter
@TableName("q_question_type")
public class QuestionType extends DataEntity {

    /**
     * 题目分类名称
     */
    @TableField("type_name")
    private String typeName;

    /**
     * 题目分类文档地址
     */
    @TableField("file_url")
    private String fileUrl;


}
